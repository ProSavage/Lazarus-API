package net.prosavage.db

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.KMongo
import com.mongodb.client.MongoCollection
import org.litote.kmongo.*

object MongoClient {

    val kmongo = KMongo.createClient()


    fun getDatabase(): MongoDatabase {
        return kmongo.getDatabase("lazarus")
    }


    fun getUserCollection(): MongoCollection<User> {
        return getDatabase().getCollection<User>()
    }

    fun registerUser(user: User) {
        getUserCollection().insertOne(user)
    }

    fun getUser(uuid: String): User? {
        return getUserCollection().findOne(User::uuid eq uuid)
    }

    fun updateUserTokens(uuid: String, amt: Int) {
        getUserCollection().updateOne(User::uuid eq uuid, setValue(User::tokens, amt))
    }

}

data class User(val uuid: String, val name: String, val tokens: Int)
