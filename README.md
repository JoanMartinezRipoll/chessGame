chessGame
=========

##WHAT THIS PROJECT IS:
This chess game enhances the open source project pocket-chess-for-android (https://code.google.com/p/pocket-chess-for-android/) by adding an offloading engine that decides wether it is faster and less enery consuming for the AI to execute the code to decide the next move localy on the Android device or remotely on a server. 

To perform this task, the code to decide the next move is copied both in the mobile device and in a server. This way, in case it is decided to offload, the required input parameters and data are sent (through 3G or Wi-Fi, LTE or whichever connection the mobile device has) to a server, causing the smart-phone to wait for the subsequent response.

##HOW THE ENGINE DECIDES:
In order to decide whether it is worth it or not to offload in a given situation, the engine estimates a
time prediction for both the local and the remote execution. The lowest time estimation determines
where the execution will be held.

More in detail, the engine decides depending on:
- Na and Sa : network and server availability respectively.
- RTT : the RTT to the server.
- Bs : network bandwidth quality for the data to be sent in bytes/ms.
- Ds : data size to be sent in bytes.
- Eta , Ets : estimation of the task’s execution time given some input parameters on the Android
and the server side respectively.

Taking the decision as it follows:

if (Na and Sa ) offloading = E ta > RTT + Ets + Ds/B

##WHY OFFLOADING IS AN INTERESSTING APPROACH FOR A CHESS GAME:

In order to decide the next move, a number of future moves n is considered. The moves are implemented
in a tree structure where each node of the tree is given an evaluation. 
Increasing the value of n will conclude to better moves, leading to a more challenging AI. However, it will also increase the cost of the calculation, favoring the probabilities to offload.

##INSTALLATION NOTES:
Import both chessAI and ChessGame projects, then run as an Android application.





