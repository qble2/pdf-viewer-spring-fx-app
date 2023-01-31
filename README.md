# pdf-viewer-spring-fx-app

A JavaFX application that is launched and managed via Spring Boot, to utilize Spring features.  
The graphical interface follows the Event-Driven Design, using Guava's Eventbus.  
The templates are created with Scene Builder.  
The project's build is managed by Maven.  
Lombok is used to reduce boilerplate code.  

## Features:
The application allows the user to
- Load and display PDF files with PDFViewFX custom control
- Split large PDF files with Apache PDFBox, based on the document's bookmarks (outlines)
- Load a directory that contains PDF files
- Filter PDF files based on the file name, with autocompletion

## Built with:
- Java 17
- [Maven](https://maven.apache.org)
- [Spring Boot 2.7.8](https://spring.io/projects/spring-boot)
- [OpenJFX 19](https://openjfx.io/)
- [JavaFX Scene Builder 19](https://gluonhq.com/products/scene-builder/)
- [Lombok](https://projectlombok.org)
- [Google Guava](https://github.com/google/guava/)
- [Apache PDFBox](https://pdfbox.apache.org/)
- [PDFViewFX](https://github.com/dlsc-software-consulting-gmbh)

## Screeshot
![Screenshot v0 2 0](https://user-images.githubusercontent.com/76587083/215354026-5380327a-9ac8-441a-bad8-9aae8628c6dd.png)
