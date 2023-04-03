>*Disclaimer:* 
>- *This project main purpose was a learning experience to tackle Spring Boot integration with JavaFX.*
>- *It may contain unpolished, experimental or rushed code that needs to be refactored and modernized.*

# PDF Viewer Spring FX  

A [JavaFX](https://openjfx.io) application that is launched and managed via [Spring Boot](https://spring.io/projects/spring-boot), to utilize Spring features.  
This application aims at easing the manipulatation of large PDF files (browse, split, filter, search and annotate).

- The graphical interface is composed of several controllers decoupled following the Event-Driven Design.  
- The templates are created with [Scene Builder](https://gluonhq.com/products/scene-builder).  
- [Apache PDFBox](https://pdfbox.apache.org) is used to manipulate PDF files.  
- The project's build is managed by [Maven](https://maven.apache.org).  
- [Lombok](https://projectlombok.org) is used to reduce boilerplate code.  

## Features:
The application allows the user to
- Load PDF files from a directory.  
- Browse loaded PDF files in a flat view or a hierachical view.  
- Filter PDF files based on the file name, with autocompletion.  
- Split large PDF files, based on the document's bookmarks (outlines).  
- Pin PDF files in a second view for later access or for comparaison purposes.  
- Add personal notes to PDF files.
- Switch between light and dark mode.  


## Built with:
- Java 17
- [Spring Boot 2.7.8](https://spring.io/projects/spring-boot)
- [OpenJFX 19](https://openjfx.io)
- [JavaFX Scene Builder 19](https://gluonhq.com/products/scene-builder)
- [Google Guava](https://github.com/google/guava)
- [Apache PDFBox](https://pdfbox.apache.org)
- [PDFViewFX](https://github.com/dlsc-software-consulting-gmbh)
- [Maven](https://maven.apache.org)
- [Lombok](https://projectlombok.org)

## Screeshot

![pdf-viewer-spring-fx-app-screenshot-1](https://user-images.githubusercontent.com/76587083/220027684-b090360f-fe09-405a-be72-a0a389e27a04.png)  

![pdf-viewer-spring-fx-app-screenshot-2](https://user-images.githubusercontent.com/76587083/220027687-84f4f3e6-6189-42ea-96cc-53d942a2e9f2.png)  

![pdf-viewer-spring-fx-app-screenshot-3](https://user-images.githubusercontent.com/76587083/220027691-6343f108-9790-49a3-8d23-13abe070de1b.png)  

![pdf-viewer-spring-fx-app-screenshot-4](https://user-images.githubusercontent.com/76587083/220027694-abdb7539-dc25-4dfb-aa1e-3f0d372b1f06.png)  

![pdf-viewer-spring-fx-app-screenshot-5](https://user-images.githubusercontent.com/76587083/220028248-b5ffa309-293d-4bcd-9c8a-8c1e837be7bf.png)  


