package DI.ViewModels
import DI.Composables.ProfileSection.AvatarVersionManager
import DI.Models.UserInfo.Profile
import DI.Models.UserInfo.UpdatedProfile
import DI.Models.UserInfo.UserAvatar
import DI.Repositories.ProfileRepository
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uploadAvatarState = MutableStateFlow<Result<String>?>(null)
    val uploadAvatarState: StateFlow<Result<String>?> = _uploadAvatarState.asStateFlow()

    private val _profile = MutableStateFlow<Result<Profile>?>(null)
    val profile: StateFlow<Result<Profile>?> = _profile.asStateFlow()

    private val _updatedProfileState = MutableSharedFlow<Boolean>()
    val updatedProfileState = _updatedProfileState.asSharedFlow()

    private val _avatarVersion = MutableStateFlow("")
    val avatarVersion: StateFlow<String> = _avatarVersion.asStateFlow()

    private val _isLoadingAvatar = MutableStateFlow(false)
    val isLoadingAvatar: StateFlow<Boolean> = _isLoadingAvatar.asStateFlow()

    private val _friendAvatars = MutableStateFlow<List<UserAvatar>>(emptyList())
    val friendAvatars: StateFlow<List<UserAvatar>> = _friendAvatars.asStateFlow()

    private val _friendAvatar = MutableStateFlow(UserAvatar("", ""))
    val friendAvatar: StateFlow<UserAvatar> = _friendAvatar.asStateFlow()

    private val _friendProfile = MutableStateFlow<Result<Profile>?>(null)
    val friendProfile: StateFlow<Result<Profile>?> = _friendProfile.asStateFlow()

    init {
        viewModelScope.launch {
            AvatarVersionManager.getAvatarVersion(context).collect  {
                _avatarVersion.value = it
            }
        }
        getProfile()
        Log.d("ProfileViewModel", "ProfileViewModel initialized")
    }

    fun uploadAvatar(file: File) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingAvatar.value = true
            val result = profileRepository.uploadAvatar(file)
            _uploadAvatarState.value = result
            if(result.isSuccess) {
                val newVersion = "v${System.currentTimeMillis()}"
                AvatarVersionManager.setAvatarVersion(context, newVersion)
                _avatarVersion.value = newVersion
            }
            _isLoadingAvatar.value = false
        }
    }

    fun getProfile() {
        Log.d("ProfileViewModel", "getProfile called")
        viewModelScope.launch {
            val result = profileRepository.getProfile()
            _profile.value = result
        }
    }

    fun updateProfile(updatedProfile: UpdatedProfile) {
        viewModelScope.launch {
            val result = profileRepository.updateProfile(updatedProfile)
            if(result.isSuccess) {
                _updatedProfileState.emit(true)
            } else {
                _updatedProfileState.emit(false)
            }
        }
    }

    fun getFriendProfile(friendId: String) {
        viewModelScope.launch {
            val result = profileRepository.getFriendProfile(friendId)
            _friendProfile.value = result
        }
    }

    fun getFriendAvatar(friendId: String) {
        viewModelScope.launch {
            _isLoadingAvatar.value = true
            val result = profileRepository.getFriendProfile(friendId)
            result.onSuccess { friendProfile ->
                _friendAvatar.value = UserAvatar(
                    userId = friendProfile.id,
                    avatarUrl = friendProfile.avatarUrl
                )
            }.onFailure {
                Log.e("GettingFriendAvatar", "Error fetching friend avatar: ${it.message}")
            }
            _isLoadingAvatar.value = false
        }
    }

    fun getFriendAvatars(friendIds: List<String>) {
        viewModelScope.launch {
            _isLoadingAvatar.value = true
            val profileList = profileRepository.getOtherUserProfiles(friendIds)
            val friendProfiles = profileList.mapNotNull { profile -> // filters only successful results
                profile.getOrNull() // null results are filtered out
            }

            // Match profiles by ID and extract avatarUrl
            val userAvatars = friendProfiles
                .filter { it.id in friendIds } // Filter out only relevant friend profiles
                .map { profile ->
                    UserAvatar(
                        userId = profile.id,
                        avatarUrl = profile.avatarUrl
                    )
                }

            _friendAvatars.value = userAvatars
            _isLoadingAvatar.value = false
        }
    }

}

