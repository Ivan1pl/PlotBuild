/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mcmiddleearth.plotbuild.command;

import org.bukkit.command.CommandSender;

/**
 *
 * @author Ivan1pl
 */
public class PlotBan extends AbstractCommand {
    
    public PlotBan(String... permissionNodes) {
        super(1, true, permissionNodes);
    }
    
    @Override
    protected void execute(CommandSender cs, String... args) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}