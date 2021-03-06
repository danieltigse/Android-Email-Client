package com.criptext.mail.scenes.settings.cloudbackup

import com.google.api.services.drive.Drive
import java.util.*

class CloudBackupModel {
    var activeAccountEmail: String = ""
    var hasCloudBackup: Boolean = false
    var lastTimeBackup: Date? = null
    var autoBackupFrequency: Int = 0
    var wifiOnly: Boolean = true
    var mDriveService: Drive? = null
    var firstTimeOpen = true
    var passphraseForEncryptedFile: String? = null
    var lastBackupSize = 0L
    var hasOldFile = false
    var oldFileId: List<String> = listOf()
    var fileLength = 0L
    var isBackupDone = false
    var localFilePath: String? = null
}