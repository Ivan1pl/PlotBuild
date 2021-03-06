/* 
 *  Copyright (C) 2015 Minecraft Middle Earth
 * 
 *  This file is part of PlotBuild.
 * 
 *  PlotBuild is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  PlotBuild is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with PlotBuild.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.mcmiddleearth.plotbuild.utils;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author Ivan1pl
 */
public class ListUtil {
    
    public static String playerListToString(List <UUID> players) {
        ArrayList <String> uuids = new ArrayList<>();
        for (UUID player : players) {
            uuids.add(player.toString());
        }
        return Joiner.on(';').join(uuids);
    }
    
    public static List <UUID> playerListFromString(String uuids) {
        ArrayList <UUID> players = new ArrayList<>();
        if(uuids.length() > 0) {
            for(String uuid : Splitter.on(';').split(uuids)) {
                players.add(UUID.fromString(uuid));
            }
        }
        return players;
    }
    
    public static List <Integer> integersFromString(String string, char delim) {
        List <String> strList = Splitter.on(delim).splitToList(string);
        ArrayList <Integer> list = new ArrayList<>();
        for(String s : strList) {
            list.add(Integer.parseInt(s));
        }
        return list;
    }

    public static String messageListToString(List<String> messages) {
        return Joiner.on(';').join(messages);
    }
    
    public static List<String> messagesFromString(String string) {
        ArrayList <String> messages = new ArrayList<>();
        if(string.length() > 0) {
            for(String message : Splitter.on(';').split(string)) {
                messages.add(message);
            }
        }
        return messages;
    }
    
}
