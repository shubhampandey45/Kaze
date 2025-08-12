package com.sp45.kaze.utils

data class SignalDataModel(
    val type: SignalDataModelTypes?=null,
    val data: String?=null
)

enum class SignalDataModelTypes{
    OFFER, ANSWER, ICE, CHAT
}

/**
 * In WebRTC, ICE stands for Interactive Connectivity Establishment.
 *
 * It’s the process WebRTC uses to figure out how two devices can connect directly over the internet, even if they’re behind routers, firewalls, or NAT (Network Address Translation).
 *
 * How ICE works (simplified):
 * Gather candidates
 *
 * A “candidate” is a possible network path (IP + port) for communication.
 *
 * These can be:
 *
 * Host candidates → Your device’s local IP (e.g., 192.168.x.x)
 *
 * Server reflexive candidates → Public IP detected via STUN server
 *
 * Relay candidates → A fallback route via TURN server when direct connection fails.
 *
 * Exchange candidates
 *
 * Candidates are sent between peers via a signaling server (e.g., WebSocket).
 *
 * Connectivity checks
 *
 * ICE tests each possible route to see which works best.
 *
 * Select best path
 *
 * Usually prefers direct, low-latency paths; falls back to TURN relay if needed.
 *
 * Why ICE is important in WebRTC:
 * Without ICE, most peers wouldn’t be able to connect directly because of NAT/firewall restrictions.
 *
 *
 */