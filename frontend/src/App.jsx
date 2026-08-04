import "./App.css";
import "./pages/tokens.css";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { HomePage } from "./pages/home/HomePage";
import { Dashboard } from "./pages/dashboard/Dashboard";
import { TransportPage } from "./pages/transport/TransportPage";
import { FoodPage } from "./pages/food/FoodPage";
import { EnergyPage } from "./pages/energy/EnergyPage";
import { LeaderboardPage } from "./pages/leaderboard/LeaderboardPage";
import { AnalyticsPage } from "./pages/analytics/AnalyticsPage";
import { LandingPage } from "./pages/dashboard/LandingPage";

function App() {
  return (
    <BrowserRouter>
      <Routes>
              <Route path="/" element={<LandingPage />} />
              <Route path="/login" element={<HomePage />} />
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/transport" element={<TransportPage />} />
              <Route path="/food" element={<FoodPage />} />
              <Route path="/energy" element={<EnergyPage />} />
              <Route path="/leaderboard" element={<LeaderboardPage />} />
              <Route path="/analytics" element={<AnalyticsPage />} />
            </Routes>
    </BrowserRouter>
  );
}

export default App;
