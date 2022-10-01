This project is still in development. To open it on your pc you should create a psql database with the following structure:
![image](https://user-images.githubusercontent.com/55293545/193403669-54f6e8c4-9fa4-43df-a63c-e04e7eff46a4.png)

Now I want to talk a little about how this project started. Me and my friends were playing TRPG (tabletop roleplaying game) and faced a problem that bothered us some time. We couldn't play ambient sounds and music at the same time with normal means. So I've decided to create one.
Firstly I've decomposed the problem and created UML diagrams that looked like theese.
![image](https://user-images.githubusercontent.com/55293545/193404009-75a01183-8c22-45ef-a21b-4caab3278c5a.png)
![image](https://user-images.githubusercontent.com/55293545/193404021-f81fbf9a-2bcd-40d6-a6b2-c45cf0d22669.png)
![image](https://user-images.githubusercontent.com/55293545/193404040-2e6ca741-f7b9-474d-8919-9d8f3d7d55f3.png)
![image](https://user-images.githubusercontent.com/55293545/193404077-0b7d8388-62a1-4589-9334-78555f67767f.png)

And right now project slightly differs but I am really thankful myself for creating these, because they were a lot of help. At this stage I've already knew that my stack will look like this: 
Spring (web mvc, data jpa, security), Websocket for exchanging data between users in room, PostgreSQL, lombok and also HTML, CSS, JS for frontend.
Right now this project is in state of mvp and I'm planning on adding a lot of features after adding some tests to it.  
If you want to tell me something - my email: pein.6948@gmail.com, tg: @resst. Don't be shy ;)
