package com.email.DB.DAO

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.email.DB.models.Email
import com.email.DB.models.EmailUser

/**
 * Created by sebas on 2/7/18.
 */

@Dao
interface EmailUserJoinDao {

    @Insert
    fun insert(emailUser : EmailUser)

    @Query("SELECT * FROM email INNER JOIN email_user ON email.id=email_user.emailId WHERE email_user.userId=:userId")
    fun getEmailsFromUser(userId: Int) : List<Email>

    @Insert
    fun insertAll(emailUsers : List<EmailUser>)

    @Query("SELECT * FROM email_user")
    fun getAll() : List<EmailUser>

    @Delete
    fun deleteAll(emailUsers: List<EmailUser>)

}
