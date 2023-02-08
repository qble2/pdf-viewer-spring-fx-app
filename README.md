# PDF Viewer Spring FX  

- A [JavaFX](https://openjfx.io) application that is launched and managed via [Spring Boot](https://spring.io/projects/spring-boot), to utilize Spring features.  
- The graphical interface is composed of several controllers decoupled following the Event-Driven Design.  
- The templates are created with [Scene Builder](https://gluonhq.com/products/scene-builder).  
- [Apache PDFBox](https://pdfbox.apache.org) is used to manipulate PDF files.  
- The project's build is managed by [Maven](https://maven.apache.org).  
- [Lombok](https://projectlombok.org) is used to reduce boilerplate code.  

## Features:
The application allows the user to
- Load PDF files from a directory.  
- Browser loaded PDF files in a flat view or hierachical view.  
- Filter PDF files based on the file name, with autocompletion.  
- Split large PDF files, based on the document's bookmarks (outlines).  
- Pin PDF files in a second view for later access or for comparaison purposes.  
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
- Main window (Light mode)  
![pdf-viewer-spring-fx-app-screenshot-1](https://user-images.githubusercontent.com/76587083/217449748-e425012a-9b22-4a6e-80b7-062907382b12.png)  

- Progress dialog (Light mode)  
![pdf-viewer-spring-fx-app-screenshot-2](https://user-images.githubusercontent.com/76587083/216746979-ab57dfdd-fc5a-424e-851b-05a084561e05.png)  

- Settings dialog (Light mode)  
![pdf-viewer-spring-fx-app-screenshot-3](https://user-images.githubusercontent.com/76587083/217449846-2aa77934-3901-4eeb-9543-235724311840.png)  

- Main window (Dark mode)  
![pdf-viewer-spring-fx-app-screenshot-4](https://user-images.githubusercontent.com/76587083/217500048-c8dfc1f1-8dfe-4c47-b229-1cd7ee493740.png)  

