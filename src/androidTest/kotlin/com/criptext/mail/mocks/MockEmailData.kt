package com.criptext.mail.mocks

import com.criptext.mail.db.DeliveryTypes
import com.criptext.mail.db.models.Email
import java.util.*

/**
 * Created by gabriel on 6/28/18.
 */
object MockEmailData {
    fun createNewEmail(dateMilis: Long, number: Int): Email =
            Email(id = number.toLong(), messageId = number.toString(),
                            threadId = "thread$number", unread = true, secure = true,
                            content = "this is message #$number", preview = "message #$number",
                            subject = "message #$number", delivered = DeliveryTypes.DELIVERED,
                            date = Date(dateMilis + number), metadataKey = number + 100L,
                            isMuted = false, unsentDate = Date(dateMilis + number))

    fun createNewEmail(number: Int) = createNewEmail(System.currentTimeMillis(), number)

    fun createNewEmails(max: Int) = (1..max).map{ createNewEmail(it)}
}