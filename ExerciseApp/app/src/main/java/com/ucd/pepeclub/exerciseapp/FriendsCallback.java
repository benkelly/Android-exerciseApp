/*
Active Go

Sam Kennan 14320061,
Benjamin Kelly 14700869,
Eoin Kerr 13366801,
Darragh Mulhall 14318776
*/
package com.ucd.pepeclub.exerciseapp;

/* Interface need to request the returning data from BackgroundDataBaseTasks
*  back to the called class which implements this
* */
public interface FriendsCallback {
    void processFinish(String output);

    void userProcessFinish(String output);

}


