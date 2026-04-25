# 📝 Guía de commits para el repositorio

**Autores:** Gustavo Ordóñez (@GaoaCorp) · Nicolás Sánchez

Ejecuta estos **5 commits** en orden después de hacer `git init` y configurar el remote a tu repo privado de GitHub. Cada commit agrupa un conjunto lógico de cambios y cumple el requisito del enunciado: **mensajes descriptivos en imperativo**.

---

## Requisito previo

```bash
# 1. Crear el repositorio en GitHub como "Gustavo-Ordonez-previo-p2" (privado) — YA CREADO
# 2. En la carpeta raíz del proyecto ejecutar:
git init
git branch -M main
git remote add origin https://github.com/GaoaCorp/Gustavo-Ordonez-previo-p2.git
```

---

## Commit 1 — Estructura inicial del proyecto

```bash
git add build.gradle.kts settings.gradle.kts gradle.properties .gitignore \
        app/build.gradle.kts app/src/main/AndroidManifest.xml \
        app/src/main/java/com/gaoacorp/microinternships/MicroInternshipsApplication.kt \
        app/src/main/res/values/ app/src/main/res/xml/ app/src/main/res/drawable/
git commit -m "Configura proyecto base con Gradle, Hilt y estructura AndroidX"
```

---

## Commit 2 — Capa de dominio (modelos + contratos)

```bash
git add app/src/main/java/com/gaoacorp/microinternships/domain/
git commit -m "Agrega modelos de dominio Task, Publisher, Result y UiState"
```

---

## Commit 3 — Capa de datos (Room + Retrofit + Repository)

```bash
git add app/src/main/java/com/gaoacorp/microinternships/data/ \
        app/src/main/java/com/gaoacorp/microinternships/di/
git commit -m "Implementa Room, Retrofit, mappers y TaskRepository con TTL de 15 min"
```

---

## Commit 4 — Capa de UI (Fragments, ViewModels, layouts)

```bash
git add app/src/main/java/com/gaoacorp/microinternships/ui/ \
        app/src/main/res/layout/ app/src/main/res/navigation/
git commit -m "Agrega pantallas de lista y detalle con estados UI y paginacion"
```

---

## Commit 5 — Documentación y evidencias

```bash
git add README.md docs/ screenshots/ COMMITS.md
git commit -m "Incluye README, documento tecnico PDF y capturas de pantalla"
```

---

## Push final

```bash
git push -u origin main
```

---

## ✅ Verificación

Después del push, revisa en GitHub que aparezcan los **5 commits** en el historial y que el repositorio esté marcado como **privado**.
