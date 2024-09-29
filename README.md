Ribbon2 is concept of news message system for journalism and news agencies. It based on older Ribbon system but with complete new tech stack. System now built around micro-service architecture.

Current version: **3.0**

### List of units
 - `gateway` - REST api facade for units and also holder or user/group data;
 - `directory-unit` - unit for holding tree of directories and permission data;
 - `message-unit` - unit for holding messages;
 - `exchanger-unit` - unit for launcing IO modules;
 - `uix` - basic UI written on HTMX;

### Key features
 - Docker support;
 - Basic UI;
 - High scability;
 - Secure auth;
 - Ability to create tree of directories for messages with transparent access control;
 - Ability to post/edit/delete message in several directories;
 - Import messages from: plain text files, mail, RSS and Telegram;
 - Export messages to: plain text files and mail;
 - Open API;

### Planned features
 - Async API for critical operations;

### Tech stack
System based on Javalin (REST API provider), MessageBus 6.0, Ebean and HTMX.

Source code of Ribbon2 system distributed under terms of the GNU LGPLv3 license.
