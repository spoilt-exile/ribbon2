Ribbon2 is concept of news message system for journalism and news agencies. It based on older Ribbon system but with complete new tech stack. System now built around micro-service architecture.

Current version: **1.0**

### List of units
 - `gateway` - REST api facade for units and also holder or user/group data;
 - `directory-unit` - unit for holding tree of directories and permission data;
 - `message-unit` - unit for holding messages;

### Key features
 - Docker support;
 - High scability;
 - Secure auth;
 - Ability to create tree of directories for messages with transparent access control;
 - Ability to post/edit/delete message in several directories;
 - Open API;

### Planned features
 - Rich message options and properties support;
 - Import/export to the system;
 - Async API for critical API;

### Tech stack
System based on Spark Java (REST API provider), MessageBus 5.2 and Ebean.

Source code of Ribbon2 system distributed under terms of the GNU LGPLv3 license.