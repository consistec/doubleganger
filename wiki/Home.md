**doubleganger** is a flexible framework for the synchronization of SQL databases.

Currently, only centralized scenarios are supported, where a central *server* synchronizes its database with several *clients*.

Whitepaper
==========

Want to learn more about the theory behind the doubleganger framework? 

1. [Introduction](Whitepaper:Introduction)
2. [User stories](Whitepaper:User-stories)
3. [The synchronization process](Whitepaper:The-synchronization-process)

Tutorial
========

Roll up your sleeves and synchronize your own databases!

1. [Requirements and goal](Tutorial:1-Requirements-and-goal)
2. [What is the doubleganger framework](Tutorial:2-What-is-the-doubleganger-framework)
3. [Getting the doubleganger framework](Tutorial:3-Getting-the-doubleganger-framework)
4. [Structure of the doubleganger project](Tutorial:4-Structure-of-the-doubleganger-project)
5. [Creating your first sync project](Tutorial:5-Creating-your-first-sync-project)

Android Client
--------------

To test the framework against slower devices, you can create your own Android client:

1. [Installation-of-required-components](AndroidSyncClient:Installation-of-required-components)
2. [Manipulate-table-content-with-Sqlite3-on-android-emulator](AndroidSyncClient:Manipulate-table-content-with-Sqlite3-on-android-emulator)
3. [GUI](AndroidSyncClient:GUI)

Command line interface
----------------------

If you are no friend of fancy UIs, we have you covered with the [command line interface](ConsoleSyncClient:Command-line-interface) to the doubleganger framework.


Architecture
============

Dive into the technical deeps of the project:

1. [Getting started for framework developers](Architecture:Getting-started-for-framework-developers)
2. [Algorithm](Architecture:Algorithm)
3. [SyncAgent](Architecture:SyncAgent)
4. [SyncProvider](Architecture:SyncProvider)
5. [Database adapters](Architecture:Database-adapters)
6. [Provider configuration properties](Architecture:Provider-configuration-properties)
7. [Framework configuration](Architecture:Framework-configuration)
8. [Logging and Tracing](Architecture:Logging-and-Tracing)

Test and Troubleshooting
========================

 * [Tests](Tests)
 * [Troubleshooting](Troubleshooting)
