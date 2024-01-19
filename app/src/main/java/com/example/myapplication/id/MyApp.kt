package com.example.myapplication.id

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

//Todas las apps que usan Hilt deben contener una clase Application anotada con @HiltAndroidApp.
//@HiltAndroidApp activa la generación de código de Hilt, incluida una clase base para tu aplicación
//que sirve como contenedor de dependencia a nivel de la aplicación.
//Se adjunta este componente generado por Hilt al ciclo de vida del objeto Application y le
//proporciona dependencias. Además, es el componente superior de la app, lo que significa que otros
//componentes pueden acceder a las dependencias que proporciona.
@HiltAndroidApp
class MyApp : Application()