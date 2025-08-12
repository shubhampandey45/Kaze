package com.sp45.kaze.utils


//Don't confuse this with StatusDataModelTypes
// These are the state of mobile app or UI
sealed class MatchState {
    data object NewState: MatchState()
    data object IDLE: MatchState()
    data object LookingForMatchState: MatchState()
    data class OfferedMatchState(val participant: String): MatchState()
    data class ReceivedMatchState(val participant: String): MatchState()
    data object Connected: MatchState()
}