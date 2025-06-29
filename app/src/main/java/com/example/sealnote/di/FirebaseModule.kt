package com.example.sealnote.di

import android.app.Application
import com.example.sealnote.R
import com.example.sealnote.data.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth // Import ktx extension
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore // Import ktx extension
import com.google.firebase.ktx.Firebase // Import Firebase root
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return Firebase.firestore
    }

    @Provides
    @Singleton
    fun provideGoogleSignInClient(app: Application): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(app.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(app, gso)
    }

    @Provides
    @Singleton
    fun provideAuthRepository( // <--- Sediakan AuthRepository
        firebaseAuth: FirebaseAuth,
        googleSignInClient: GoogleSignInClient,
        firestore: FirebaseFirestore,
        application: Application
    ): AuthRepository {
        return AuthRepository(firebaseAuth, googleSignInClient, firestore, application)
    }
}