package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Registration
import com.example.data.model.ResultRecord
import com.example.data.model.Tournament
import com.example.data.model.Transaction
import com.example.data.model.UserProfile
import com.example.data.model.Wallet
import com.example.data.model.Withdrawal
import com.example.data.repository.EsportsRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Tournaments : Screen("tournaments", "Tournaments")
    object MyMatches : Screen("my_matches", "My Matches")
    object Leaderboard : Screen("leaderboard", "Leaderboard")
    object Wallet : Screen("wallet", "Wallet")
    object Profile : Screen("profile", "Profile")
    object Admin : Screen("admin", "Admin Panel")
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository = EsportsRepository(application)

    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    private val _wallet = MutableStateFlow<Wallet?>(null)
    val wallet: StateFlow<Wallet?> = _wallet.asStateFlow()

    private val _tournaments = MutableStateFlow<List<Tournament>>(emptyList())
    val tournaments: StateFlow<List<Tournament>> = _tournaments.asStateFlow()

    private val _myRegistrations = MutableStateFlow<List<Registration>>(emptyList())
    val myRegistrations: StateFlow<List<Registration>> = _myRegistrations.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _withdrawals = MutableStateFlow<List<Withdrawal>>(emptyList())
    val withdrawals: StateFlow<List<Withdrawal>> = _withdrawals.asStateFlow()

    private val _allWithdrawals = MutableStateFlow<List<Withdrawal>>(emptyList())
    val allWithdrawals: StateFlow<List<Withdrawal>> = _allWithdrawals.asStateFlow()

    private val _results = MutableStateFlow<List<ResultRecord>>(emptyList())
    val results: StateFlow<List<ResultRecord>> = _results.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage: SharedFlow<String> = _snackbarMessage.asSharedFlow()

    // For Admin override / view
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun showMessage(msg: String) {
        viewModelScope.launch {
            _snackbarMessage.emit(msg)
        }
    }

    fun onUserAuthenticated(user: FirebaseUser) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val profile = repository.ensureUserAndWallet(user)
                _userProfile.value = profile
                _isAdmin.value = repository.isCurrentUserAdmin() || profile.role == "admin"

                // Seed sample data if empty
                repository.seedSampleDataIfEmpty()

                // Observe Profile
                launch {
                    repository.observeUserProfile(user.uid).collect { p ->
                        if (p != null) {
                            _userProfile.value = p
                            _isAdmin.value = repository.isCurrentUserAdmin() || p.role == "admin"
                        }
                    }
                }

                // Observe Wallet
                launch {
                    repository.observeWallet(user.uid).collect { w ->
                        _wallet.value = w
                    }
                }

                // Observe Tournaments
                launch {
                    repository.observeTournaments().collect { list ->
                        _tournaments.value = list
                    }
                }

                // Observe My Registrations
                launch {
                    repository.observeUserRegistrations(user.uid).collect { regs ->
                        _myRegistrations.value = regs
                    }
                }

                // Observe Transactions
                launch {
                    repository.observeUserTransactions(user.uid).collect { txs ->
                        _transactions.value = txs
                    }
                }

                // Observe Withdrawals
                launch {
                    repository.observeUserWithdrawals(user.uid).collect { wList ->
                        _withdrawals.value = wList
                    }
                }

                // Observe Results
                launch {
                    repository.observeResults().collect { rList ->
                        _results.value = rList
                    }
                }

                // Observe Admin Withdrawals if Admin
                launch {
                    repository.observeAllWithdrawals().collect { allW ->
                        _allWithdrawals.value = allW
                    }
                }

            } catch (e: Exception) {
                showMessage("Sync error: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun joinTournament(
        tournament: Tournament,
        playerUid: String,
        inGameName: String,
        teammates: List<String> = emptyList(),
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val profile = _userProfile.value
            val userName = profile?.name ?: "Player"

            val result = repository.joinTournament(
                tournament = tournament,
                playerUid = playerUid,
                inGameName = inGameName,
                userName = userName,
                teammates = teammates
            )

            _isLoading.value = false
            result.onSuccess {
                showMessage("Successfully joined ${tournament.name}! Slot confirmed.")
                onSuccess()
            }.onFailure { err ->
                showMessage(err.message ?: "Failed to join tournament")
            }
        }
    }

    fun addCoins(amount: Long, packTitle: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.addCoins(amount, "Added Widara Coins", packTitle)
            _isLoading.value = false
            result.onSuccess {
                showMessage("+$amount Widara Coins added to your wallet!")
            }.onFailure { err ->
                showMessage(err.message ?: "Coin purchase failed")
            }
        }
    }

    fun requestWithdrawal(amount: Long, upiId: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val profile = _userProfile.value
            val userName = profile?.name ?: "Player"
            val result = repository.requestWithdrawal(userName, amount, upiId)
            _isLoading.value = false
            result.onSuccess {
                showMessage("Withdrawal request for $amount Widara Coins submitted! Admin will verify.")
                onSuccess()
            }.onFailure { err ->
                showMessage(err.message ?: "Withdrawal request failed")
            }
        }
    }

    fun updateProfile(name: String, mobile: String, ffUid: String, ign: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateProfile(name, mobile, ffUid, ign)
            _isLoading.value = false
            result.onSuccess {
                showMessage("Profile updated successfully!")
                onSuccess()
            }.onFailure { err ->
                showMessage(err.message ?: "Profile update failed")
            }
        }
    }

    // Admin Actions
    fun adminCreateTournament(tournament: Tournament, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.createTournament(tournament)
            _isLoading.value = false
            result.onSuccess {
                showMessage("Tournament '${tournament.name}' published!")
                onSuccess()
            }.onFailure { err ->
                showMessage(err.message ?: "Failed to create tournament")
            }
        }
    }

    fun adminSetRoomDetails(tournamentId: String, roomId: String, roomPass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateTournamentRoom(tournamentId, roomId, roomPass)
            _isLoading.value = false
            result.onSuccess {
                showMessage("Room ID & Password broadcasted to players!")
                onSuccess()
            }.onFailure { err ->
                showMessage(err.message ?: "Failed to update room details")
            }
        }
    }

    fun adminUpdateStatus(tournamentId: String, status: String) {
        viewModelScope.launch {
            val result = repository.updateTournamentStatus(tournamentId, status)
            result.onSuccess {
                showMessage("Tournament status updated to $status")
            }.onFailure { err ->
                showMessage(err.message ?: "Status update failed")
            }
        }
    }

    fun adminDeleteTournament(tournamentId: String) {
        viewModelScope.launch {
            val result = repository.deleteTournament(tournamentId)
            result.onSuccess {
                showMessage("Tournament deleted.")
            }.onFailure { err ->
                showMessage(err.message ?: "Delete failed")
            }
        }
    }

    fun adminProcessWithdrawal(w: Withdrawal, approve: Boolean, note: String) {
        viewModelScope.launch {
            val result = repository.adminProcessWithdrawal(w, approve, note)
            result.onSuccess {
                showMessage("Withdrawal ${if (approve) "Approved" else "Rejected"}.")
            }.onFailure { err ->
                showMessage(err.message ?: "Processing failed")
            }
        }
    }

    fun adminAdjustCoins(userId: String, amount: Long, isAdd: Boolean, reason: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val result = repository.adminAdjustCoins(userId, amount, isAdd, reason)
            result.onSuccess {
                showMessage("Adjusted $amount Widara Coins for user.")
                onSuccess()
            }.onFailure { err ->
                showMessage(err.message ?: "Coin adjustment failed")
            }
        }
    }

    fun adminPublishResult(result: ResultRecord, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val res = repository.adminPublishResult(result)
            res.onSuccess {
                showMessage("Tournament Result published! Winner credited ${result.prizeAmount} Coins.")
                onSuccess()
            }.onFailure { err ->
                showMessage(err.message ?: "Publishing result failed")
            }
        }
    }
}
