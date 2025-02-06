package app.withyourwallet.vote.models

import java.time.LocalDateTime

class Score {
    var id: Int = 0

    var subjectId: Int = 0

    var number: Int = 0

    var topic: String = ""

    var headline: String = ""

    var sourceUrl: String = ""

    var createdAt: LocalDateTime? = null

    var createdByUserName: String = ""
}
