# IB2026 DavidSC — Prácticas Android Iberdrola

Proyecto Android desarrollado como práctica interna de Viewnext para Iberdrola.
Autor: David SC | Tutor: Viewnext

---

## Descripción

Aplicación Android nativa en Kotlin que simula una app de gestión de facturas de energía (luz y gas). El proyecto se desarrolla en cuatro entregas iterativas; este README cubre el estado correspondiente a la **segunda entrega**.

---

## Segunda entrega — funcionalidad implementada

La segunda entrega hace funcional el sistema de filtrado de facturas, añadiéndose sobre todo lo implementado en la primera entrega.

### Pantalla de filtros (`FilterScreen`)

- **Filtrado por fecha**: campos "Desde" y "Hasta" con date picker nativo de Material 3. Los selectores impiden seleccionar rangos incoherentes (la fecha de inicio no puede ser posterior a la de fin y viceversa). Ambos campos pueden borrarse individualmente para quitar el criterio de fecha.
- **Filtrado por importe**: slider de rango doble cuyos límites mínimo y máximo se calculan dinámicamente a partir de los importes reales de las facturas cargadas en ese momento. El rango se recalcula al cambiar de pestaña (Luz / Gas) o de calle.
- **Filtrado por estado**: checkboxes múltiples — Pagada, Pendiente de Pago, En trámite de cobro, Anulada, Cuota Fija. Pueden combinarse libremente.
- **Botón "Aplicar filtros"**: aplica todos los criterios activos y vuelve a `InvoicesScreen`.
- **Botón "Borrar filtros"**: elimina cualquier filtro activo y vuelve a `InvoicesScreen`.

### Integración con `InvoicesScreen`

- El botón de filtrar del histórico de facturas está habilitado cuando hay facturas del tipo seleccionado, y deshabilitado en caso contrario.
- El botón se muestra relleno (verde) cuando hay al menos un filtro activo, y con borde cuando no hay ninguno.
- Al volver de `FilterScreen`, la lista refleja inmediatamente los filtros aplicados sin necesidad de recargar datos de red.
- Los filtros se conservan al cambiar de pestaña (Luz / Gas), pero se pierden al salir de `InvoicesScreen` (navegación hacia atrás).

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
│   └── utils          → AppConfig (flags globales de configuración), Screen
├── data
│   ├── local          → Room: InvoiceDatabase, InvoiceDao, InvoiceEntity
│   ├── remote         → Retrofit + Retromock: InvoiceApi, DTOs, AssetBodyFactory
│   ├── mapper         → Conversión DTO ↔ dominio ↔ entidad
│   └── repository     → InvoiceRepositoryImpl
├── domain
│   ├── model          → Invoice, InvoiceType, InvoiceFilter
│   ├── repository     → InvoiceRepository (interfaz)
│   └── usecase        → GetInvoicesUseCase, GetStreetsUseCase
├── ui
│   ├── main           → MainScreen, MainViewModel
│   ├── invoices       → InvoicesScreen, InvoicesViewModel,
│   │                    FilterScreen, InvoiceComponents, RatingBottomSheet
│   └── util           → CurrencyFormatter, DateFormatter
└── di                 → NetworkModule, DatabaseModule, RepositoryModule,
                         UseCaseModule, CoroutineModule
```

**Principios aplicados:**

- La capa `domain` no tiene ninguna dependencia de Android ni de librerías externas.
- `ui` y `data` dependen de `domain`, nunca al revés.
- Cada caso de uso tiene una única responsabilidad.
- Los repositorios se definen como interfaces en `domain` y se implementan en `data`.
- El filtrado ocurre en el ViewModel sobre los datos ya cargados en memoria, evitando llamadas de red adicionales al cambiar los criterios.

---

## Stack tecnológico

| Tecnología | Uso |
|---|---|
| Kotlin | Lenguaje principal |
| Jetpack Compose | UI declarativa |
| Hilt | Inyección de dependencias |
| Room | Caché local / modo offline |
| Retrofit | Llamadas HTTP a Mockoon |
| Retromock | Mocks locales desde assets |
| Mockoon | Servidor HTTP local de mocks |
| StateFlow / combine | Estado reactivo en ViewModels |
| Navigation Component | Navegación Single Activity |
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
AGP 9.1.0 → requiere KSP (no kapt)
Hilt 2.59.2+ → compatibilidad con AGP 9.1
Retromock 1.1.1
Compose BOM 2024.12.01
Room 2.7.0
Retrofit 2.11.0
KSP 2.2.10-2.0.2
```

---

## Modo mock local vs Mockoon

La app incluye un flag `AppConfig.useMockLocal` que controla la fuente de datos:

- **`useMockLocal = true`**: Retromock sirve los JSON desde la carpeta `assets/`. Se aplica un delay aleatorio de 1-3 s para simular latencia real. El skeleton de carga aparece durante ese tiempo.
- **`useMockLocal = false`**: Retrofit apunta a `https://10.0.2.2:3001/` (Mockoon ejecutándose en el host, accesible desde el emulador vía HTTPS con certificado autofirmado). Para probar este modo, toma el JSON ubicado en `res/raw`, impórtalo en Mockoon y arranca el endpoint local antes de desactivar el mock local.

El estado del toggle se persiste en `SharedPreferences` y se restaura en cada arranque.

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
| `entrega/3` | (próximas entregas) |
| `entrega/4` | (próximas entregas) |

El repositorio es público y se comparte con los tutores desde el primer commit.

---

## Pruebas manuales realizadas

| Escenario | Estado |
|---|---|
| Rotación portrait ↔ landscape | Funciona (estado estable) |
| Abrir/cerrar rápido la app | OK |
| Navegación rápida main → facturas → filtros → back | OK |
| Volver atrás múltiples veces (bottom sheet rating) | OK, lógica de conteo correcta |
| Background → foreground | OK, estado conservado |
| Lista vacía tras aplicar filtros | Mensaje informativo visible |
| Sin conexión | Los datos de caché se sirven correctamente |
| Datos corruptos o nulos en mock | OK, mapper descarta registros inválidos |
| Filtro fecha con rango de un único día | OK |
| Filtro importe con min == max | OK, aunque se queda clipeado si pulsas lejos |
| Combinación de los tres filtros | OK |
| Borrar filtros restaura la lista completa | OK |
| Cambio de pestaña Luz/Gas con filtro activo | Filtro se conserva; slider se recalcula manteniendo intención de usuario |
| Navegar rápido a filtros antes de que cargue la pantalla anterior | OK, protección contra doble navegación |

---

## Próximas entregas

- **Entrega 3:** Flujo de activación/modificación de factura electrónica por contrato (selección de contrato, pantalla de activación, validación de email y código OTP simulado).
- **Entrega 4:** Integración con Firebase: Remote Config, Google Analytics y Crashlytics.