
import org.junit.Assert;
import org.junit.Test;
import tk.freaxsoftware.ribbon2.core.data.DirectoryModel;

/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020-2022 Freax Software
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

/**
 * Test for directory model.
 * @author Stanislav Nepochatov
 */
public class DirectoryModelTest {
    
    @Test
    public void shouldGetParentNameFirstLevel() {
        DirectoryModel model = new DirectoryModel();
        model.setName("System");
        model.setFullName("System");
        String parentName = model.parentName();
        Assert.assertTrue(parentName.isEmpty());
    }
    
    @Test
    public void shouldGetParentNameSecondLevel() {
        DirectoryModel model = new DirectoryModel();
        model.setName("Test");
        model.setFullName("System.Test");
        String parentName = model.parentName();
        Assert.assertEquals(parentName, "System");
    }
    
    @Test
    public void shouldGetParentNameThirdLevel() {
        DirectoryModel model = new DirectoryModel();
        model.setName("Inner");
        model.setFullName("System.Test.Inner");
        String parentName = model.parentName();
        Assert.assertEquals(parentName, "System.Test");
    }
    
}
