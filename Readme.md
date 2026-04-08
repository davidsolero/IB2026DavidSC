# IB2026 DavidSC — Prácticas Android Iberdrola

Proyecto Android desarrollado como práctica interna de Viewnext para Iberdrola.
Autor: David SC | Tutor: Viewnext

---

## Descripción

Aplicación Android nativa en Kotlin que simula una app de gestión de facturas de energía (luz y gas). El proyecto se desarrolla en cuatro entregas iterativas; este README cubre el estado correspondiente a la **primera entrega**.

---

## Primera entrega — funcionalidad implementada

La primera entrega cubre íntegramente la pantalla de listado de facturas, tal como especifica el documento de prácticas:

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
│   └── utils          → AppConfig (flags globales de configuración)
├── data
│   ├── local          → Room: InvoiceDatabase, InvoiceDao, InvoiceEntity
│   ├── remote         → Retrofit + Retromock: InvoiceApi, DTOs, AssetBodyFactory
│   ├── mapper         → Conversión DTO → dominio
│   └── repository     → InvoiceRepositoryImpl
├── domain
│   ├── model          → Invoice, InvoiceType
│   ├── repository     → InvoiceRepository (interfaz)
│   └── usecase        → GetInvoicesUseCase, GetStreetsUseCase
├── ui
│   ├── main           → MainScreen, MainViewModel
│   └── invoices       → InvoicesScreen, InvoicesViewModel,
│                         InvoiceComponents, RatingBottomSheet
└── di                 → Módulos Hilt: Network, Database, Repository, UseCase, Coroutine
```

**Principios aplicados:**

- La capa `domain` no tiene ninguna dependencia de Android ni de librerías externas.
- `ui` y `data` dependen de `domain`, nunca al revés.
- Cada caso de uso tiene una única responsabilidad.
- Los repositorios se definen como interfaces en `domain` y se implementan en `data`.

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
| StateFlow | Estado reactivo en ViewModels |
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
```

---

## Modo mock local vs Mockoon

La app incluye un flag `AppConfig.useMockLocal` que controla la fuente de datos:

- **`useMockLocal = true`**: Retromock sirve los JSON desde la carpeta `assets/`. Se aplica un delay aleatorio de 1-3 s para simular latencia real. El skeleton de carga aparece durante ese tiempo.
- **`useMockLocal = false`**: Retrofit apunta a `http://10.0.2.2:3001/` (Mockoon ejecutándose en el host, accesible desde el emulador).

El estado del toggle se persiste en `SharedPreferences` y se restaura en cada arranque.

---

## Tests unitarios

Se incluyen tests unitarios para las capas de dominio, datos y ViewModel:

| Fichero | Qué cubre |
|---|---|
| `GetInvoicesUseCaseTest` | Filtrado por tipo, por calle y combinado |
| `GetStreetsUseCaseTest` | Calles únicas y ordenadas |
| `InvoiceMapperTest` | Mapeo DTO → dominio y excepción en tipo desconocido |
| `InvoiceRepositoryImplTest` | Carga desde API + caída al cache en fallo |
| `InvoicesViewModelTest` | Carga inicial, cambio de tipo, estado de error, toggleMock, lógica back |
| `MainScreenTest` | Carga de calles, estado de error y estados de loading en MainViewModel |

Los tests usan **fake repositories** (implementaciones manuales de la interfaz de dominio) y `FakeSharedPreferences` para evitar dependencias de Android en el entorno JVM.

---

## Gestión de ramas (Git)

| Rama | Propósito |
|---|---|
| `main` | Solo se toca en el momento de entrega |
| `develop` | Trabajo del día a día; rama estable |
| `entrega/1` | Snapshot de la primera entrega, creada desde `develop` |
| `entrega/2` | (próximas entregas) |
| `entrega/3` | (próximas entregas) |
| `entrega/4` | (próximas entregas) |

El repositorio es público y se comparte con los tutores desde el primer commit.

---

## Pruebas manuales realizadas

| Escenario | Estado |
|---|---|
| Rotación portrait ↔ landscape | Funciona (estado estable) |
| Abrir/cerrar rápido la app | OK |
| Navegación rápida main → facturas → back | OK |
| Volver atrás múltiples veces (bottom sheet rating) | OK, lógica de conteo correcta |
| Background → foreground | OK, estado conservado |
| Lista vacía | Mensaje informativo visible |
| Sin conexión (modo avión) | Los datos de caché se sirven correctamente |
| Datos corruptos o nulos en mock | Pendiente de verificación |

---

## Próximas entregas

- **Entrega 2:** Filtrado por fecha, importe y estado desde una pantalla dedicada.
- **Entrega 3:** Flujo de activación/modificación de factura electrónica por contrato.
- **Entrega 4:** Integración con Firebase: Remote Config, Google Analytics y Crashlytics.