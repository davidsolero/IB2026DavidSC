# IB2026 DavidSC — Prácticas Android Iberdrola

Proyecto Android desarrollado como práctica interna de Viewnext para Iberdrola.
Autor: David SC | Tutor: Viewnext

---

## Descripción

Aplicación Android nativa en Kotlin que simula una app de gestión de facturas de energía (luz y gas) con soporte para factura electrónica. El proyecto se desarrolla en cuatro entregas iterativas; este README cubre el estado correspondiente a la **cuarta entrega**.

---

## Cuarta entrega — funcionalidad implementada

La cuarta entrega integra Firebase en la aplicación sobre todo lo implementado en las entregas anteriores.

### Remote Config

- Variable booleana `gas_contracts_enabled` que habilita o deshabilita los contratos de gas.
- Por defecto habilitados tanto en Firebase como en el valor local de fallback.
- El flag se consume en `GetContractsUseCase`: si está deshabilitado, los contratos de tipo GAS se excluyen antes de devolver la lista.
- El fetch se realiza en `App.onCreate()` con intervalo mínimo de 1 hora en producción y 0 segundos en debug para facilitar las pruebas.

### Google Analytics

- Screen views automáticos en cada cambio de destino del NavController, registrados desde `MainActivity` via `OnDestinationChangedListener`.
- Eventos de botón trackeados a través de `AnalyticsTracker`, singleton inyectado en los ViewModels:

| Evento | Origen |
|---|---|
| `ver_todas_facturas` | Botón de acceso global a facturas en `MainScreen` |
| `ver_facturas_calle` | Acceso a facturas por calle en `MainScreen` |
| `gestionar_factura_electronica` | Botón de entrada al flujo de contratos |
| `toggle_mock` | Cambio de fuente de datos en `MainScreen` |
| `aplicar_filtros` | Botón aplicar en `FilterScreen` |
| `borrar_filtros` | Botón borrar en `FilterScreen` |
| `modificar_email` | Botón de modificación en `ActiveContractScreen` |
| `reenviar_otp` | Cada reenvío de código SMS en `OtpVerificationScreen` |
| `forzar_crash` | Botón de crash en `MainScreen` |

### Crashlytics

- Integrado mediante el plugin de Gradle de Crashlytics.
- Botón de bug (icono rojo) en la esquina inferior derecha de `MainScreen` que, tras confirmación en un diálogo, fuerza un crash para generar un informe en la consola de Firebase.

---

## Tercera entrega — funcionalidad implementada

La tercera entrega añade el flujo completo de gestión de factura electrónica sobre todo lo implementado en las entregas anteriores.

### Punto de entrada

- Botón "Gestionar factura electrónica" en `MainScreen`, debajo del acceso a todas las facturas.

### Pantalla de selección de contrato (`ContractSelectionScreen`)

- Lista de contratos del usuario (uno de luz, uno de gas), cargados desde un JSON mock en assets.
- Cada contrato muestra su tipo, dirección y estado (Activo / Sin activar) con badge de color.
- Al pulsar un contrato activo se navega a `ActiveContractScreen`; si está inactivo, a `ActivateContractScreen`.

### Pantalla de contrato activo (`ActiveContractScreen`)

- Muestra el email enmascarado actualmente vinculado al contrato.
- Botón "Modificar email" que inicia el wizard de modificación.

### Pantalla de activación de contrato (`ActivateContractScreen`)

- Muestra el email enmascarado vinculado a la cuenta del usuario.
- Campo de texto para introducir el email de facturación.
- Bloque de información básica sobre protección de datos con enlaces a Iberdrola.
- Checkbox de aceptación de condiciones legales.
- Botón "Siguiente" habilitado únicamente cuando el email es válido y el checkbox está marcado.

### Pantalla de modificación de email (`ModifyEmailScreen`)

- Campo de texto para introducir el nuevo email de facturación.
- Botón "Siguiente" habilitado únicamente cuando el email introducido es válido.

### Pantalla de verificación OTP (`OtpVerificationScreen`)

- Campo para introducir el código de verificación de 6 dígitos.
- Bloque informativo con opción de reenvío del SMS (máximo 3 intentos por sesión).
- Tras agotar los 3 intentos, el enlace se deshabilita y se muestra un contador regresivo de 24 horas persistido en `SharedPreferences`.
- Banner de confirmación temporal al reenviar correctamente.
- Overlay de carga mientras se simula el reenvío.
- Botón "Siguiente" habilitado solo cuando el código tiene 6 dígitos.

### Pantalla de confirmación (`ConfirmationScreen`)

- Pantalla de fondo verde con imagen de confirmación.
- El título y el cuerpo se adaptan según si el flujo fue de activación o de modificación de email.
- Muestra el email nuevo enmascarado.
- El botón "Aceptar" y el botón de cierre redirigen a `ContractSelectionScreen`.
- El botón de sistema (back) redirige igualmente a `ContractSelectionScreen`, no al paso anterior.

### Flujo de modificación de email

El flujo completo es: `ActiveContractScreen` → `ModifyEmailScreen` → `OtpVerificationScreen` → `ConfirmationScreen`. Al confirmar, el email del contrato se actualiza en la caché en memoria del repositorio y queda reflejado inmediatamente si el usuario vuelve a `ActiveContractScreen`.

### Componentes compartidos del flujo

- `FlowHeader`: cabecera con botón de cierre, título y barra de progreso lineal que indica en qué paso del wizard se encuentra el usuario.
- `ContractNavigationButtons`: botones "Anterior" y "Siguiente" reutilizados en todas las pantallas del wizard.
- `ContractEmailField`: campo de texto con estilo de línea inferior, usado tanto para email como para el código OTP.
- El botón de cierre de `FlowHeader` navega siempre directamente a `ContractSelectionScreen`, descartando todos los pasos intermedios del back stack.

### Enmascaramiento de email

La función `maskEmail()` en `ui/util/EmailUtils.kt` aplica la regla: primer carácter + asteriscos + último carácter + dominio completo. Ejemplo: `pepe2@gmail.com → p****2@gmail.com`.

### Navegación y protección contra doble tap

Todas las pantallas del flujo utilizan el patrón de flag `isExiting` (booleano local que se activa en el primer evento de navegación y nunca se resetea) para evitar pantallas en blanco, crashes por taps rápidos y doble navegación.

---

## Segunda entrega — funcionalidad implementada

La segunda entrega hace funcional el sistema de filtrado de facturas, añadiéndose sobre todo lo implementado en la primera entrega.

### Pantalla de filtros (`FilterScreen`)

- **Filtrado por fecha**: campos "Desde" y "Hasta" con date picker nativo de Material 3. Los selectores impiden seleccionar rangos incoherentes. Ambos campos pueden borrarse individualmente.
- **Filtrado por importe**: el usuario ajusta un control de rango con dos puntos para seleccionar el intervalo de importes que desea ver. Los extremos que se muestran se calculan automáticamente a partir de las facturas disponibles (siempre reflejan el mínimo y máximo reales). El rango elegido se recuerda por pestaña (Luz / Gas): al cambiar de pestaña la app restaura el rango previamente usado para esa pestaña y muestra un aviso temporal con el rango restaurado. Si el usuario borra los filtros, el control vuelve a mostrar los valores mínimos y máximos detectados.
- **Filtrado por estado**: checkboxes múltiples — Pagada, Pendiente de Pago, En trámite de cobro, Anulada, Cuota Fija.
- **Botón "Aplicar filtros"**: aplica todos los criterios activos y vuelve a `InvoicesScreen`.
- **Botón "Borrar filtros"**: elimina cualquier filtro activo y vuelve a `InvoicesScreen`.

### Integración con `InvoicesScreen`

- El botón de filtrar está habilitado cuando hay facturas del tipo seleccionado, y deshabilitado en caso contrario.
- El botón se muestra relleno (verde) cuando hay al menos un filtro activo, y con borde cuando no hay ninguno.
- Al volver de `FilterScreen`, la lista refleja inmediatamente los filtros sin recargar datos de red.
- Los filtros se conservan al cambiar de pestaña (Luz / Gas), pero se pierden al salir de `InvoicesScreen`.

### Comunicación entre pantallas

`FilterScreen` consume el mismo `InvoicesViewModel` que `InvoicesScreen`, obtenido del `BackStackEntry` de la ruta de facturas. Los filtros activos son estado del ViewModel, no argumentos de navegación.

### JSON mock ampliado

Los mocks locales (Retromock) y el servidor Mockoon incluyen al menos una factura de cada estado: Pagada, Pendiente de Pago, En trámite de cobro, Anulada y Cuota Fija.

---

## Primera entrega — funcionalidad implementada

La primera entrega cubre íntegramente la pantalla de listado de facturas:

- **Pantalla principal** (`MainScreen`): selección de contrato por calle o acceso global a todas las facturas.
- **Pantalla de facturas** (`InvoicesScreen`): cabecera con navegación, pestañas Luz/Gas, tarjeta de última factura y listado histórico agrupado por año.
- **Skeleton de carga**: se muestra mientras se obtienen los datos, tanto en portrait como en landscape.
- **Diálogo de factura no disponible**: al pulsar sobre cualquier factura aparece un diálogo informativo.
- **Bottom sheet de valoración**: al salir de la pantalla de facturas se solicita opinión al usuario según las reglas de frecuencia:
  - Valoración dada → vuelve a preguntar a las 10 salidas.
  - "Responder más tarde" → vuelve a preguntar a las 3 salidas.
  - Bottom sheet cerrado sin responder → pregunta en la siguiente salida.
- **Modo mock local / Mockoon**: botón en cabecera para alternar entre datos locales (Retromock, con delay simulado de 1-3 s) y servidor HTTP local (Mockoon). El estado se persiste entre sesiones con `SharedPreferences`.
- **Caché local con Room**: los datos se guardan en base de datos local y se sirven como fallback si la red falla.
- **Orientación landscape**: layout adaptado con la última factura a la izquierda y el listado a la derecha.

---

## Arquitectura

El proyecto sigue **Clean Architecture + MVVM** con separación estricta de capas.

```
com.iberdrola.practicas2026.davidsc
├── core
│   └── utils          → AppConfig, OtpFlow, DeviceUtils, Screen, AnalyticsTracker
├── data
│   ├── local          → Room: InvoiceDatabase, InvoiceDao, InvoiceEntity
│   ├── remote         → Retrofit + Retromock: InvoiceApi, DTOs, AssetBodyFactory
│   ├── mapper         → Conversión DTO ↔ dominio ↔ entidad (facturas y contratos)
│   └── repository     → InvoiceRepositoryImpl, ContractRepositoryImpl
├── domain
│   ├── model          → Invoice, InvoiceType, InvoiceFilter, Contract, ContractType
│   ├── repository     → InvoiceRepository, ContractRepository (interfaces)
│   └── usecase        → GetInvoicesUseCase, GetStreetsUseCase,
│                        GetContractsUseCase, UpdateContractEmailUseCase
├── ui
│   ├── main           → MainScreen, MainViewModel
│   ├── invoices       → InvoicesScreen, InvoicesViewModel,
│   │                    FilterScreen, InvoiceComponents, RatingBottomSheet
│   ├── contract       → ContractSelectionScreen, ContractSelectionViewModel,
│   │                    ActiveContractScreen, ActivateContractScreen,
│   │                    ModifyEmailScreen, OtpVerificationScreen, OtpViewModel,
│   │                    ConfirmationScreen, ContractDetailViewModel,
│   │                    FlowHeader, ContractComponents
│   ├── navigation     → AppNavHost, SafeNavController, Screen
│   └── util           → CurrencyFormatter, DateFormatter, EmailUtils
└── di                 → NetworkModule, DatabaseModule, RepositoryModule,
                         UseCaseModule, CoroutineModule, ContractModule,
                         AnalyticsModule
```

**Principios aplicados:**

- La capa `domain` no tiene ninguna dependencia de Android ni de librerías externas.
- `ui` y `data` dependen de `domain`, nunca al revés.
- Cada caso de uso tiene una única responsabilidad.
- Los repositorios se definen como interfaces en `domain` y se implementan en `data`.
- El filtrado de facturas ocurre en el ViewModel sobre los datos ya cargados en memoria.
- `ContractModule` está instalado en `SingletonComponent` para que la caché en memoria del repositorio sobreviva a la recreación de ViewModels entre navegaciones.
- `AnalyticsTracker` es un singleton inyectado en los ViewModels; la UI no tiene conocimiento directo de Firebase.

---

## Stack tecnológico

| Tecnología | Uso |
|---|---|
| Kotlin | Lenguaje principal |
| Jetpack Compose | UI declarativa |
| Hilt | Inyección de dependencias |
| Room | Caché local / modo offline (facturas) |
| Retrofit | Llamadas HTTP a Mockoon |
| Retromock | Mocks locales desde assets |
| Mockoon | Servidor HTTP local de mocks |
| StateFlow / combine | Estado reactivo en ViewModels |
| Navigation Component | Navegación Single Activity |
| SharedPreferences | Persistencia de flags y estado OTP |
| Firebase Remote Config | Flag de habilitación de contratos de gas |
| Firebase Analytics | Tracking de pantallas y botones |
| Firebase Crashlytics | Informes de errores en producción |
| `kotlinx-coroutines-test` | Tests de corrutinas |
| MockK | Mocking en tests unitarios |

---

## Configuración del proyecto

- **Nombre de app:** IB2026 DavidSC
- **Package:** `com.iberdrola.practicas2026.davidsc`
- **SDK mínimo:** API 29 (Android 10)
- **Lenguaje:** Kotlin
- **IDE:** Android Studio

### Dependencias destacadas y versiones

```
AGP 9.1.0            → requiere KSP (no kapt)
Hilt 2.59.2+         → compatibilidad con AGP 9.1
KSP 2.2.10-2.0.2
Retromock 1.1.1
Compose BOM 2024.12.01
Room 2.7.0
Retrofit 2.11.0
Firebase BOM 33.7.0
```

---

## Modo mock local vs Mockoon

La app incluye un flag `AppConfig.useMockLocal` que controla la fuente de datos de facturas:

- **`useMockLocal = true`**: Retromock sirve los JSON desde la carpeta `assets/`. Se aplica un delay aleatorio de 1-3 s para simular latencia real.
- **`useMockLocal = false`**: Retrofit apunta a `https://10.0.2.2:3001/` (Mockoon ejecutándose en el host, accesible desde el emulador vía HTTPS con certificado autofirmado). Para probar este modo, importa el JSON de `res/raw/mockoon_iberdrola.json` en Mockoon y arranca el endpoint local.

Los contratos siempre se cargan desde `assets/contracts_mock.json` independientemente de este flag.

### Uso de Mockoon en dispositivo físico

La redirección del puerto se realiza automáticamente al instalar la app desde Android Studio
(Run → Run 'app'). No es necesario ejecutar ningún comando manualmente.

Si por algún motivo necesitas hacerlo a mano:
```
adb reverse tcp:3001 tcp:3001
```

---

## Firebase

El proyecto Firebase está asociado a la aplicación y compartido con los tutores de Viewnext.

### Remote Config

El parámetro `gas_contracts_enabled` controla la visibilidad de los contratos de gas en la pantalla de selección de contratos. Para probarlo:

1. Ve a Firebase Console → Remote Config
2. Cambia el valor de `gas_contracts_enabled` a `false` y publica los cambios
3. Reinicia la app; el contrato de gas dejará de aparecer en la lista

### Analytics — DebugView

Para ver eventos en tiempo real durante desarrollo:

```
adb shell setprop debug.firebase.analytics.app com.iberdrola.practicas2026.davidsc
```

Luego ve a Firebase Console → Analytics → DebugView.

---

## Tests unitarios

| Fichero | Qué cubre |
|---|---|
| `GetInvoicesUseCaseTest` | Filtrado por tipo, por calle y combinado |
| `GetStreetsUseCaseTest` | Calles únicas y ordenadas |
| `InvoiceMapperTest` | Mapeo DTO → dominio y tipo desconocido |
| `InvoiceRepositoryImplTest` | Carga desde API + caída al caché en fallo |
| `InvoicesViewModelTest` | Carga inicial, cambio de tipo, estado de error, toggleMock, lógica back |
| `InvoicesFilterViewModelTest` | Filtrado por estado, fecha, importe, combinado; clearFilter; isFilterActive; casos límite |
| `MainScreenTest` | Carga de calles, estado de error y estados de loading en MainViewModel |

Los tests usan **fake repositories** (implementaciones manuales de la interfaz de dominio) y `FakeSharedPreferences` para evitar dependencias de Android en el entorno JVM.

---

## Gestión de ramas (Git)

| Rama | Propósito |
|---|---|
| `main` | Solo se toca en el momento de entrega |
| `develop` | Trabajo del día a día; rama estable |
| `entrega/1` | Snapshot de la primera entrega, creada desde `develop` |
| `entrega/2` | Snapshot de la segunda entrega, creada desde `develop` |
| `entrega/3` | Snapshot de la tercera entrega, creada desde `develop` |
| `entrega/4` | Snapshot de la cuarta entrega, creada desde `develop` |

El repositorio es público y se comparte con los tutores desde el primer commit.

---

## Pruebas manuales realizadas

| Escenario | Estado |
|---|---|
| Rotación portrait ↔ landscape | OK |
| Abrir/cerrar rápido la app | OK |
| Navegación rápida main → facturas → filtros → back | OK |
| Volver atrás múltiples veces (bottom sheet rating) | OK |
| Background → foreground | OK, estado conservado |
| Lista vacía tras aplicar filtros | Mensaje informativo visible |
| Sin conexión | Los datos de caché se sirven correctamente |
| Datos corruptos o nulos en mock | OK, mapper descarta registros inválidos |
| Filtro fecha con rango de un único día | OK |
| Combinación de los tres filtros | OK |
| Borrar filtros restaura la lista completa | OK |
| Cambio de pestaña Luz/Gas con filtro activo | Filtro se conserva; slider se recalcula |
| Navegar rápido a filtros antes de que cargue la pantalla anterior | OK, isExiting evita doble navegación |
| Flujo activación de contrato completo | OK |
| Flujo modificación de email completo | OK, email actualizado en caché |
| Back desde ConfirmationScreen | Redirige a ContractSelectionScreen, no al paso anterior |
| Cierre (X) desde cualquier pantalla del wizard | Redirige a ContractSelectionScreen |
| Reenvío OTP hasta agotar intentos | Enlace deshabilitado, contador regresivo visible |
| Persistencia del bloqueo OTP entre sesiones | OK, restaurado desde SharedPreferences |
| Taps rápidos en botones de navegación del wizard | OK, isExiting previene doble navegación |
| Email actualizado visible al volver a ActiveContractScreen | OK |
| Remote Config gas deshabilitado oculta contrato de gas | OK |
| Remote Config gas habilitado muestra ambos contratos | OK |
| Eventos Analytics visibles en DebugView | OK |
| Crash forzado genera informe en Crashlytics | OK |
