package com.example.chesstimer

/*Not a extension function*/

class TimerMethods {

    fun fischerMethod(currentTime: Long, incrementSeconds: Long): Long {
        return currentTime + incrementSeconds
    }

    fun bronsteinMethod(
        currentTime: Long,
        turnStartSeconds: Long,
        pauseTime: Long,
        incrementSeconds: Long
    ): Long {
        val totalTurnTimeUsed = turnStartSeconds - currentTime + pauseTime
        val returnedTime = minOf(totalTurnTimeUsed, incrementSeconds)
        return currentTime + returnedTime
    }

}