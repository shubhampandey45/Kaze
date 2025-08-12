package com.sp45.kaze.remote

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.sp45.kaze.utils.FirebaseFieldNames
import com.sp45.kaze.utils.MatchState
import com.sp45.kaze.utils.MyValueEventListener
import com.sp45.kaze.utils.SharedPrefHelper
import com.sp45.kaze.utils.SignalDataModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseClient @Inject constructor(
    private val database: DatabaseReference,
    private val prefHelper: SharedPrefHelper,
    private val gson: Gson
) {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    /**
     * Starts listening to the current user's status in Firebase.
     * Updates Firebase with "LookingForMatch" state and triggers the callback
     * whenever the status changes (e.g., matched, connected, idle).
     */
    fun observeUserStatus(callback: (MatchState) -> Unit) {
        coroutineScope.launch {
            //remove self data
            removeSelfData()
            // update self status to looking for match
            updateSelfStatus(StatusDataModel(type = StatusDataModelTypes.LookingForMatch))

            val statusRef = database
                .child(FirebaseFieldNames.USERS)
                .child(prefHelper.getUserId())
                .child(FirebaseFieldNames.STATUS) // USERS/{userId}/STATUS

            statusRef.addValueEventListener(object : MyValueEventListener() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    super.onDataChange(snapshot)
                    // Firebase SDK converts DB data into our Kotlin class
                    snapshot.getValue(StatusDataModel::class.java)?.let { status ->
                        val newState = when (status.type) {
                            StatusDataModelTypes.IDLE -> MatchState.IDLE
                            StatusDataModelTypes.LookingForMatch -> MatchState.LookingForMatchState
                            StatusDataModelTypes.OfferedMatch -> MatchState.OfferedMatchState(status.participant!!)
                            StatusDataModelTypes.ReceivedMatch -> MatchState.ReceivedMatchState(
                                status.participant!!
                            )

                            StatusDataModelTypes.Connected -> MatchState.Connected
                            else -> null
                        }

                        // If newState is not null
                        newState?.let {
                            callback(it)
                        }
                            ?: coroutineScope.launch { // if is null then update self status to looking for match
                                updateSelfStatus(StatusDataModel(type = StatusDataModelTypes.LookingForMatch))
                                callback(MatchState.LookingForMatchState)
                            }
                    } ?: coroutineScope.launch {
                        updateSelfStatus(StatusDataModel(type = StatusDataModelTypes.LookingForMatch))
                        callback(MatchState.LookingForMatchState)
                    }
                }

            })
        }
    }

    fun observeIncomingSignals(callback: (SignalDataModel) -> Unit) {
        database.child(FirebaseFieldNames.USERS).child(prefHelper.getUserId())
            .child(FirebaseFieldNames.DATA).addValueEventListener(object : MyValueEventListener() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    super.onDataChange(snapshot)
                    runCatching {
                        gson.fromJson(snapshot.value.toString(), SignalDataModel::class.java)
                    }.onSuccess {
                        if (it != null) callback(it)
                    }.onFailure {
                        Log.d("TAG", "onDataChange: ${it.message}")
                    }
                }
            })
    }

    suspend fun updateParticipantDataModel(participantId: String, data: SignalDataModel) {
        database.child(FirebaseFieldNames.USERS).child(participantId).child(FirebaseFieldNames.DATA)
            .setValue(gson.toJson(data)).await()
    }

    suspend fun updateParticipantStatus(participantId: String, status: StatusDataModel) {
        database.child(FirebaseFieldNames.USERS).child(participantId)
            .child(FirebaseFieldNames.STATUS).setValue(status).await()
    }

    /**
     * When eventListener works and user enters DB
     * its status is marked as LookingForMatch
     * Now we want other LookingForMatch to connect with
     * other LookingForMatch users
     */
    suspend fun findNextMatch() {
        removeSelfData()  // remove data to provide new session
        findAvailableParticipant { foundTarget ->
            Log.d("TAG", "findNextMatch: $foundTarget")
            foundTarget?.let { target ->
                coroutineScope.launch {
                    updateSelfStatus(StatusDataModel(participant = target, type = StatusDataModelTypes.OfferedMatch))
                    database
                        .child(FirebaseFieldNames.USERS)
                        .child(target)
                        .child(FirebaseFieldNames.STATUS)
                        .setValue(
                            StatusDataModel(
                                participant = prefHelper.getUserId(),
                                type = StatusDataModelTypes.ReceivedMatch
                            )
                        )
                        .await()

                }
            }
        }
    }

    private fun findAvailableParticipant(callback: (String?) -> Unit) {
        database
            .child(FirebaseFieldNames.USERS)
            .orderByChild("status/type") // searching status type in firebase DB
            .equalTo(StatusDataModelTypes.LookingForMatch.name)
            .addListenerForSingleValueEvent(object : MyValueEventListener() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    super.onDataChange(snapshot)
                    var foundTarget: String? = null
                    snapshot.children.forEach { childSnapShot ->
                        if (childSnapShot.key != prefHelper.getUserId()) {
                            foundTarget = childSnapShot.key
                            return@forEach
                        }
                    }
                    if (foundTarget != null) {
                        callback(foundTarget)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            }
            )
    }

    suspend fun updateSelfStatus(status: StatusDataModel) {
        database.child(FirebaseFieldNames.USERS)
            .child(prefHelper.getUserId())
            .child(FirebaseFieldNames.STATUS)
            .setValue(status)
            .await()
    }

    suspend fun removeSelfData() {
        database.child(FirebaseFieldNames.USERS).child(prefHelper.getUserId())
            .child(FirebaseFieldNames.DATA).removeValue().await()
    }

    fun clear() {
        coroutineScope.cancel()
    }
}

