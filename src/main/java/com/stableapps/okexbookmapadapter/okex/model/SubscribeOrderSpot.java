/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.stableapps.okexbookmapadapter.okex.model;

import java.util.List;

import lombok.Data;

/**
 *
 * @author aris
 */
@Data
public class SubscribeOrderSpot extends Message {

    public String table;
    public List<OrderDataSpot> data;
}
