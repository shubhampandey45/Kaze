package com.sp45.kaze.remote

data class StatusDataModel(
    val participant: String? = null,
    val type: StatusDataModelTypes? = null
)

//Don't confuse this with MatchState
// These are the data object that we store in our firebase database
enum class StatusDataModelTypes{
    IDLE, LookingForMatch, OfferedMatch, ReceivedMatch, Connected
}