# EcoSort AI — App Context & Feature Documentation

## 1. What is EcoSort AI?
**EcoSort AI** is an intelligent, AI-powered smart waste segregation, recycling assistant, and environmental telemetry Android app built using **Kotlin** and **Jetpack Compose (Material Design 3)**.

The primary objective of the application is to eliminate waste contamination at the source, educate users on proper recycling habits, connect people with specialized local disposal centers, and monitor smart municipal bins in real time.

---

## 2. Core Capabilities & What It Can Do

### 🔍 1. AI Vision Waste Scanner & Classifier
* **Multimodal Image Recognition**: Users take a photo using their device camera, choose an image from the gallery, or select test samples.
* **Instant Material & Stream Identification**: Uses Google Gemini multimodal vision intelligence to analyze the item's material, recyclability status, and confidence score.
* **Standardized Color-Coded Bin Mapping**:
  * 🔵 **Recyclable (Blue Bin)**: Clean plastics, aluminum cans, paper, glass jars, cardboard.
  * 🟢 **Organic / Compost (Green Bin)**: Food scraps, vegetable waste, coffee grounds, garden clippings.
  * 🟡 **E-Waste (Yellow/Orange Bin)**: Cables, circuit boards, discarded smartphones, broken appliances.
  * 🔴 **Hazardous (Red Bin)**: Batteries, paint cans, medicines, chemical containers, aerosol spray cans.
  * ⚫ **General / Landfill (Grey Bin)**: Contaminated packaging, composite non-recyclable materials.
* **Disposal Preparation Protocols**: Provides actionable cleaning and safety guidelines (e.g., *"Rinse out liquid residues"*, *"Tape lithium battery terminals"*, *"Flatten cardboard packaging"*).

---

### 🗺️ 2. Interactive Eco Map & Recycling Center Locator
* **Custom Vector Canvas Map**: Interactive 2D vector map rendering local city streets, parks, water bodies, user location, and drop-off facility pins without third-party map lock-in.
* **Proximity & Route Navigation**: Computes geodesic distances (Haversine formula) from user coordinates to certified nearby recycling centers.
* **Stream Filtering & Center Profiles**: Filter centers by accepted material types (e-waste, hazardous chemicals, metals, plastics), check operational status (Open/Closed), direct phone dialer, and navigation shortcuts.

---

### 📡 3. Smart Dustbin IoT Telemetry & Monitoring
* **Municipal & Household IoT Sensors**: Real-time telemetry monitoring for connected smart waste bins across city hubs.
* **Fill-Level & Overflow Tracking**: Ultrasonic fill percentage gauges with visual warnings when bins exceed 80% capacity.
* **Remote Bin Controls & Dispatch**: Lid lock/unlock toggle, battery health monitoring, and one-tap automated municipal collection dispatch.

---

### 📖 4. Comprehensive Segregation & Bin Guide
* **Searchable Waste Encyclopedia**: Quickly search everyday household items to find exact disposal rules.
* **Contamination Prevention**: Highlights common recycling misconceptions (e.g., greasy pizza boxes vs. clean cardboard) to prevent facility batch rejection.

---

### 🏆 5. Eco Ranks, Gamification & Community Leaderboard
* **Eco-Points & Milestones**: Earn points for every logged waste scan, proper disposal action, and drop-off check-in.
* **Environmental Impact Metrics**: Calculates cumulative carbon offset (kg CO₂ saved) and diverted landfill volume.
* **Badges & Achievements**: Unlock awards like *"Zero Hero"*, *"Plastic Slayer"*, and *"Battery Guardian"*.
* **Competitive Leaderboards**: Weekly, monthly, and all-time neighborhood rankings.

---

### 📜 6. Offline History & Activity Log
* **Local Persistence**: Scanned items, classifications, and recycling timestamps are stored locally using Android Room Database and DataStore.
* **Personal Recycling Trends**: Review previous scans, bin classifications, and personal sorting statistics over time.

---

## 3. Technology Stack & Architecture

| Layer | Technologies Used |
|---|---|
| **Language & Platform** | Kotlin (100%), Android SDK 34+ |
| **UI Framework** | Jetpack Compose, Material Design 3 (M3) |
| **Architecture** | MVVM (Model-View-ViewModel), Repository Pattern, Clean Architecture |
| **Reactive State** | Kotlin Coroutines, StateFlow, collectAsStateWithLifecycle |
| **AI / Multimodal** | Google Gemini Multimodal Vision API |
| **Data Persistence** | Android Room Database (SQLite via KSP), Preferences DataStore |
| **Graphics & Mapping** | Jetpack Compose Vector Canvas |
| **Testing** | Robolectric JVM Unit Testing Suite |
| **Release Artifact** | Signed Release Android App Bundle (`app-release.aab`) |
