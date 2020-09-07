/*
 * This file is part of Ribbon2 news message system.
 * 
 * Copyright (C) 2020 Freax Software
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
package tk.freaxsoftware.ribbon2.exchanger.repository;

import io.ebean.DB;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import tk.freaxsoftware.ribbon2.exchanger.entity.Register;

/**
 * Message register repository.
 * @author Stanislav Nepochatov
 */
public class RegisterRepository {
    
    /**
     * Get readed messages register records by module, scheme and list of ids.
     * @param moduleId id of IO module;
     * @param scheme scheme for IO operation;
     * @param ids id of processed message;
     * @return map with key as message id and values as register record;
     */
    public Map<String, Register> findReadedMessages(String moduleId, String scheme, Set<String> ids) {
        List<Register> registered = DB.find(Register.class).where().eq("moduleId", moduleId).and()
                .eq("scheme", scheme).and().in("messageId", ids).findList();
        return registered.isEmpty() ? Collections.EMPTY_MAP : registered.stream()
                .collect(Collectors.toMap(Register::getMessageId, Function.identity()));
    }
    
    /**
     * Save record of message processing.
     * @param moduleId id of module;
     * @param messageId id of message (IOMessage);
     * @param header header of the message;
     * @param directory directory of processing (import or export);
     * @param scheme scheme name of IO operation config;
     * @param uid external uid of message;
     */
    public void saveRegisterRecord(String moduleId, String messageId, String header, String directory, String scheme, String uid) {
        DB.save(new Register(moduleId, messageId, header, directory, scheme, uid));
    }
    
}
