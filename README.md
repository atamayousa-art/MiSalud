# Mi Salud — Android (prototipo 1.0 beta)

Aplicación local de salud familiar inspirada visualmente en el collage proporcionado por el usuario. Proyecto **nuevo** e independiente de MediPeques. Java + vistas Android nativas + SQLite; sin servicios externos, sin cuentas y sin permisos de Internet.

## Funciones incluidas

- Alta de pacientes con **nombre completo, nombre corto, edad y sexo obligatorios**. Peso, estatura y alergias opcionales. Editar perfil y agregar más pacientes. Nombre corto rosa para mujer y azul para hombre.
- Medicamentos por paciente: nombre, presentación (tabletas, jarabe, cápsulas, inhalador/disparos, gotas, etc.), instrucciones, descripción opcional de para qué sirve, intervalo en horas, primera fecha/hora, fecha final, fotografía de cámara o archivo.
- Recordatorios locales mediante AlarmManager y notificaciones con acciones **Lo tomó / No lo tomó / +10 min**. La app también permite registrar manualmente cada toma, con historial y confirmación ante duplicados.
- Pantalla **Hoy** con medicamentos programados y consultas del día.
- Signos vitales: temperatura (°C), presión (mmHg, sistólica y diastólica), glucosa (mg/dL), fecha y hora, gráficas locales.
- Diario de síntomas: síntoma, inicio, intensidad, notas.
- Consultas previas y futuras: fecha, médico, lugar, notas, recordatorio y adjuntos (diagnóstico y receta, cámara o archivo).
- Exámenes: tipo, fecha, resultado escrito, fotografía o PDF.
- Historial cronológico integrado de tomas, mediciones, síntomas, citas y exámenes.

## Crear el repositorio en GitHub (sin Android Studio)

1. En GitHub, **New repository** > nombre `MiSalud` > **Private** > **Create repository**. No hace falta crear un README inicial.
2. Descarga y descomprime `MiSalud_Android_GitHub.zip` en tu computadora.
3. En la pestaña **Code** del repositorio nuevo, selecciona **uploading an existing file** o **Add file > Upload files**.
4. **Sube el contenido de la carpeta `MiSalud_Android`**, no la carpeta exterior. GitHub web permite arrastrar carpetas desde el explorador; verifica que las rutas se conserven, especialmente `.github/workflows/android-apk.yml` (carpeta oculta).
5. Confirma con **Commit changes** en `main`.
6. Abre **Actions > Compilar Mi Salud APK**. La compilación se ejecuta automáticamente tras el commit; también puedes usar **Run workflow**.
7. Si la ejecución termina verde, entra en ella, baja a **Artifacts**, descarga `MiSalud-debug-apk`, descomprime e instala `app-debug.apk` en tu Android.

**Importante:** El APK de este ZIP es *código fuente*, **no está precompilado ni firmado para distribución**. GitHub Actions lo compilará. El identificador `com.misalud.app` es distinto del de MediPeques, así que ambas aplicaciones pueden coexistir.

## Limitaciones y seguridad de esta primera beta

- **No es un dispositivo médico ni reemplaza indicaciones profesionales.** La app nunca prescribe dosis. Comprueba cuidadosamente nombre, presentación, dosis, intervalo y fechas contra la receta antes de activar alarmas.
- Las alarmas utilizan `setAndAllowWhileIdle` para evitar solicitar permiso especial de alarmas exactas, pero **pueden retrasarse** por el ahorro de batería o restricciones del fabricante. No se garantiza puntualidad al minuto; comprobar con pantalla bloqueada, permisos de notificación y ajustes de batería.
- Los intervalos de medicamentos se expresan en **horas fijas**. No cubre todavía pautas complejas (por ejemplo, solo lunes, horarios diferentes cada día, PRN/según necesidad). No utilizar esta beta para tratamientos críticos que dependan de una alarma exacta.
- La descripción «¿Para qué sirve?» es **manual**, no se obtiene automáticamente de una base farmacológica verificada. No se incluye IA en esta versión.
- La foto tomada con cámara es una **miniatura** para identificación rápida, no una copia documental de alta resolución. Para recetas o resultados legibles se recomienda adjuntar un PDF o una imagen ya guardada.
- Los adjuntos escogidos con el selector de documentos usan permisos persistentes. Si el archivo original se elimina o mueve, podría dejar de abrirse. Las fotos tomadas dentro de la app se guardan en su almacenamiento privado.
- **No hay sincronización, exportación ni respaldo**. Desinstalar la aplicación o borrar sus datos elimina los registros. La opción `allowBackup=false` protege frente a copias automáticas no deseadas, pero exige desarrollar un respaldo cifrado antes de uso prolongado.
- Los registros locales se guardan en SQLite en el espacio privado de la app; **todavía no hay cifrado adicional de base de datos ni bloqueo biométrico**. Para una versión pública deben añadirse controles de seguridad, accesibilidad, pruebas, política de privacidad y revisión de cumplimiento para datos sanitarios.
- Esta es una **beta para pruebas familiares**. No está lista para comercializarse ni se ha validado clínicamente.

## Estructura

```
MiSalud_Android/
  .github/workflows/android-apk.yml
  settings.gradle.kts
  build.gradle.kts
  gradle.properties
  app/build.gradle.kts
  app/src/main/AndroidManifest.xml
  app/src/main/java/com/misalud/app/
    MainActivity.java
    Db.java
    AlarmReceiver.java
    BootReceiver.java
```

## Próximas mejoras

Exportación/recuperación cifrada, horarios flexibles, alarmas exactas opcionales, edición/eliminación de registros con confirmación, gráficos con etiquetas temporales, fotos de alta resolución, información farmacológica verificada y pruebas de accesibilidad.
