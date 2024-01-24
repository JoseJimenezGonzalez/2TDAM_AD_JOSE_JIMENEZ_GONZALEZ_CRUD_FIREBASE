package com.example.accesodatos

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//@InstallIn(SingletonComponent::class):
//Significado: Esta anotación indica en qué componente de Dagger Hilt debe instalarse el módulo que sigue.
//En este caso: SingletonComponent::class especifica que este módulo (FirebaseModule) se instalará en el
//componente SingletonComponent. El SingletonComponent es un componente de Dagger Hilt que dura durante
//toda la vida de la aplicación y se utiliza para gestionar instancias únicas (singletons) de las dependencias.

//@Module:
//Significado: Esta anotación marca la clase como un módulo Dagger Hilt.
//En este caso: La clase FirebaseModule es un módulo Dagger Hilt que contiene métodos anotados con
//@Provides. Estos métodos proveen instancias de clases que pueden ser inyectadas en otras partes de la aplicación.
@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {

    //@Provides->Proporciona una instancia de DatabaseReference, StorageReference
    //@Singleton->Indica que la instancia debe de ser unica en toda la aplicacion(singleton)

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): DatabaseReference {
        return FirebaseDatabase.getInstance().reference
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): StorageReference {
        return FirebaseStorage.getInstance().reference
    }
}