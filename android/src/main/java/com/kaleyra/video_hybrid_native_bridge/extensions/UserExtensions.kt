// Copyright © 2018-2023 Kaleyra S.p.a. All Rights Reserved.
// See LICENSE for licensing information

package com.kaleyra.video_hybrid_native_bridge.extensions

import android.net.Uri
import com.kaleyra.video_common_ui.model.UserDetails
import com.kaleyra.video_hybrid_native_bridge.repository.UserDetailsEntity

/**
 *
 * @author kristiyan
 */
internal fun UserDetailsEntity.toSDK(): UserDetails {
    return UserDetails(
        userId = userAlias,
        name = nickName ?: userAlias,
        image = imageUrl?.let { Uri.parse(it) } ?: Uri.EMPTY
    )
}

internal fun com.kaleyra.video_hybrid_native_bridge.UserDetails.toSDK(): UserDetails {
    return UserDetails(
        userId = userID,
        name = nickName ?: userID,
        image = profileImageURL?.let { Uri.parse(it) } ?: Uri.EMPTY
    )
}

internal fun com.kaleyra.video_hybrid_native_bridge.UserDetails.toDatabaseEntity(): UserDetailsEntity {
    return UserDetailsEntity(
        userAlias = userID,
        nickName = nickName,
        imageUrl = profileImageURL
    )
}

internal fun UserDetailsEntity.toUserDetails(): com.kaleyra.video_hybrid_native_bridge.UserDetails = com.kaleyra.video_hybrid_native_bridge.UserDetails(
    email = this.email,
    userID = this.userAlias,
    firstName = this.firstName,
    lastName = this.lastName,
    nickName = this.nickName,
    profileImageURL = this.imageUrl
)

internal fun UserDetails.toDatabaseEntity(): UserDetailsEntity = UserDetailsEntity(userId, null, null, name, null, image.toString())
