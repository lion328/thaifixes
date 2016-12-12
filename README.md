ThaiFixes
=========

[![Build Status](https://travis-ci.org/lion328/ThaiFixes.svg?branch=master)](https://travis-ci.org/lion328/ThaiFixes)

ช่วยให้การใช้งานของภาษาไทยใน Minecraft ดูดีขึ้น

**เว็บไซต์:** http://thaifixes.lion328.com  
**ผู้จัดทำ:** [lion328](http://lion328.com) (ผู้พัฒนาและผู้ดูแล), [PCXD](http://pcxd.me) (สำหรับรหัสต้นฉบับสำหรับ Minecraft 1.2.4 และคำแนะนำต่างๆ), [secretdataz](https://github.com/secretdataz) (สำหรับรหัสต้นฉบับบางตัวและคำแนะนำต่างๆ)

วิธีการคอมไพล์
------------

1. ติดตั้ง [Java Development Kit 7](http://www.oracle.com/technetwork/java/javase/downloads/index.html) ขึ้นไป และ [Git](http://git-scm.com)
2. หากไม่ได้อยู่ในโหมด command line ให้เปิด Command Prompt สำหรับ Windows, Terminal สำหรับ Linux, ฯลฯ
3. เปลี่ยนตำแหน่งของไดเรกทอรี่ไปยังที่ที่ต้องการ
4. โคลน Git ของ ThaiFixes โดยใช้คำสั่ง `git clone https://github.com/lion328/ThaiFixes.git`
  - หากจะคอมไพล์ branch อื่นที่นอกเหนือจาก `master` ให้ทำการเปลี่ยน branch โดยการใช้คำสั่ง `git checkout [ชื่อ branch]` เช่น `git checkout forge-1.8`
5. ดาวน์โหลดรหัสต้นฉบับของ Forge เวอร์ชันล่าสุด และแตกไฟล์ทั้งหมดยกเว้นโฟลเดอร์ `src` และไฟล์ `build.gradle` ลงในโฟลเดอร์ `ThaiFixes`
6. ใน command line ใช้คำสั่ง `cd ThaiFixes` เพื่อเข้าไปยังโฟลเดอร์ `ThaiFixes`
7. ใช้คำสั่ง `gradlew.bat -Dfile.encoding=utf-8 clean build` สำหรับ Windows หรือ `./gradlew -Dfile.encoding=utf-8 clean build` สำหรับ Linux และอื่นๆ
  - เมื่ออยู่บน Linux และระบปฏิบัติการที่คล้ายกับ Unix หากไฟล์ `gradlew` ไม่ได้ตั้งให้สามารถรันได้ สามารถตั้งให้สามารถรันได้โดยการใช้คำสั่ง `chmod +x gradlew`
8. หากขึ้นว่า `BUILD SUCCESSFUL` ให้เข้าไปที่โฟลเดอร์ `build/libs` จะมีไฟล์ mod ที่คอมไพล์เสร็จเรียบร้อยแล้วออกมา
  - หากไม่ได้ขึ้นเช่นนั้นหรือขึ้นว่า `BUILD FAILED` กรุณาแก้ข้อผิดพลาดที่เกิดขึ้นตามที่โปรแกรมได้บอกเอาไว้ หรือจะแจ้งผู้พัฒนา ThaiFixes ก็ได้

ความเข้ากันกับ Minecraft เวอร์ชันอื่นๆ
-------------------------------

ในแพ็กเกจ `com.lion328.thaifixes.coremod.mapper` จะเป็นพวกคลาสที่ใช้สร้างแผนที่สำหรับชื่อที่ถูกเข้ารหัส (obfuscated) กับชื่อที่ไม่ถูกเข้ารหัส และในไฟล์ `assets\thaifixes\config\classmap\classlist` จะเป็นไฟล์ที่เก็บรายชื่อของคลาสที่สร้างแผนที่ โดยจะทำงานจากบนลงล่าง หากคลาสใดใช้ได้เป็นคลาสแรกจะใช้คลาสนั้นเป็นคลาสสร้างแผนที่

ความเข้ากันกับ Mod อื่น
-----------------------

ทางผู้พัฒนา ThaiFixes พยายามทำทุกวิธีทางเพื่อที่จะให้ mod ที่มีปัญหาความเข้ากันให้ใช้งานได้พร้อมกับ ThaiFixes หากมี mod ตัวใดใช้งานไม่ได้กรุณาติดต่อมาทาง issue ของ Github ทางเราจะพิจารณาว่าควรแก้หรือไม่

Mod ที่ ThaiFixes สนับสนุน
---------------------
1. [OptiFine](http://optifine.net/)
