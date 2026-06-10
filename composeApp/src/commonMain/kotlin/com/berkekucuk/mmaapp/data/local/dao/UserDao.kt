package com.berkekucuk.mmaapp.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.berkekucuk.mmaapp.data.local.entity.BlockedUserEntity
import com.berkekucuk.mmaapp.data.local.entity.UserEntity
import com.berkekucuk.mmaapp.data.local.relation.UserProfileRelation
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUser(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id NOT IN (SELECT blocked_user_id FROM blocked_users WHERE blocker_user_id = :currentUserId) ORDER BY total_points DESC, created_at ASC LIMIT :limit OFFSET :offset")
    fun getUsers(limit: Int, offset: Int, currentUserId: String): Flow<List<UserEntity>>

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserProfile(userId: String): Flow<UserProfileRelation?>

    @Upsert
    suspend fun upsertUsers(users: List<UserEntity>)

    @Query("UPDATE users SET username = :username, full_name = :fullName, avatar_url = :avatarUrl WHERE id = :userId")
    suspend fun updateUser(userId: String, fullName: String, username: String, avatarUrl: String)

    @Query("DELETE FROM users WHERE id = :userId")
    suspend fun deleteUser(userId: String)

    @Query("DELETE FROM users WHERE id NOT IN (:retainedIds) AND id != :currentUserId")
    suspend fun deleteUsersExcept(retainedIds: List<String>, currentUserId: String)

    @Transaction
    suspend fun replaceUsers(users: List<UserEntity>, currentUserId: String) {
        val newIds = users.map { it.id }
        upsertUsers(users)
        if (newIds.isNotEmpty()) {
            deleteUsersExcept(retainedIds = newIds, currentUserId = currentUserId)
        }
    }

    @Query("SELECT * FROM users WHERE id IN (SELECT blocked_user_id FROM blocked_users WHERE blocker_user_id = :currentUserId) ORDER BY created_at ASC")
    fun getBlockedUsers(currentUserId: String): Flow<List<UserEntity>>

    @Upsert
    suspend fun upsertBlockedUser(blockedUser: BlockedUserEntity)

    @Query("DELETE FROM blocked_users WHERE blocker_user_id = :blockerUserId AND blocked_user_id = :blockedUserId")
    suspend fun unblockUser(blockerUserId: String, blockedUserId: String)
}