# FilmTracker
## SantaBarbara Project

**En memoria de nuestro compañero Rodrigo Santabárbara Murrieta (2004-2025)**

## 1. Objetivo del Proyecto
El proyecto SantaBarbara tiene como objetivo principal el desarrollo de un ecosistema multiplataforma sincronizado. El núcleo del sistema es un servidor robusto desarrollado en Node.js, diseñado para centralizar la lógica de negocio y la persistencia de datos, sirviendo como única fuente de verdad para dos clientes distintos:

* Cliente Móvil: Una aplicación nativa desarrollada en Kotlin a través de Android Studio.
* Cliente de Escritorio: Una aplicación robusta desarrollada en Java (Netbeans).

## 2. Arquitectura del Sistema
El sistema utiliza un patrón de arquitectura distribuida donde el servidor actúa como un API Gateway hacia servicios externos (como TV Maze) y gestiona la base de datos compartida que permite la persistencia cruzada entre los dispositivos móviles y de escritorio.

## 3. Equipo de Desarrollo
* Genaro Alejandro Barradas Sánchez
* Omar Morales García
* Martinez Ramirez Alejandro

---
*Este proyecto está bajo licencia.
© 2026 Lotso Studios. Todos los derechos reservados ®™.*
