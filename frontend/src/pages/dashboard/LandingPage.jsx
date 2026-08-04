import { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './LandingPage.css';

function useCountUp(target, durationMs = 1400) {
  const [value, setValue] = useState(0);
  const ref = useRef(null);

  useEffect(() => {
    const el = ref.current;
    if (!el) return;

    let hasRun = false;
    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting && !hasRun) {
            hasRun = true;
            const start = performance.now();
            const tick = (now) => {
              const progress = Math.min((now - start) / durationMs, 1);
              const eased = 1 - Math.pow(1 - progress, 3);
              setValue(Math.floor(eased * target));
              if (progress < 1) requestAnimationFrame(tick);
            };
            requestAnimationFrame(tick);
          }
        });
      },
      { threshold: 0.3 }
    );

    observer.observe(el);
    return () => observer.disconnect();
  }, [target, durationMs]);

  return [value, ref];
}

const FEATURES = [
  {
    icon: 'transport',
    title: 'Log your transport',
    desc: 'Record commutes and trips by mode. See exactly what moving around costs you.',
  },
  {
    icon: 'food',
    title: 'Track what you eat',
    desc: 'Veg, non-veg, or junk — log it and watch how diet shapes your monthly total.',
  },
  {
    icon: 'energy',
    title: "Note today's energy",
    desc: 'One number a day. Units in, emissions out, no spreadsheets required.',
  },
  {
    icon: 'analytics',
    title: 'Read the trend',
    desc: 'Month over month, category by category — where your footprint actually comes from.',
  },
  {
    icon: 'trophy',
    title: 'Compare on the board',
    desc: "See where you rank against everyone else tracking their trail this month.",
  },
];

const ICONS = {
  transport: (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6">
      <path d="M4 16h16M5 16v-4l1.5-4.5A2 2 0 0 1 8.4 6h7.2a2 2 0 0 1 1.9 1.5L19 12v4M6 16a1.5 1.5 0 1 0 3 0 1.5 1.5 0 0 0-3 0Zm9 0a1.5 1.5 0 1 0 3 0 1.5 1.5 0 0 0-3 0Z" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  ),
  food: (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6">
      <path d="M7 3v7a2 2 0 0 0 2 2v9M7 3v7M9 3v7M17 3c-1.5 0-2.5 1.5-2.5 4s1 4 2.5 4v10" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  ),
  energy: (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6">
      <path d="M13 2 4 14h6l-1 8 9-12h-6l1-8Z" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  ),
  analytics: (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6">
      <path d="M4 20V10M11 20V4M18 20v-7" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  ),
  trophy: (
    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6">
      <path d="M8 4h8v4a4 4 0 0 1-8 0V4ZM8 5H5a1 1 0 0 0-1 1c0 2.5 1.8 4.5 4 4.9M16 5h3a1 1 0 0 1 1 1c0 2.5-1.8 4.5-4 4.9M10 15v3h4v-3M8 21h8" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  ),
};

export function LandingPage() {
  const navigate = useNavigate();
  const [usersCount, usersRef] = useCountUp(10482);
  const [co2Count, co2Ref] = useCountUp(52140);
  const [treesCount, treesRef] = useCountUp(5210);

  return (
    <div className="landing">
      <header className="landing-nav">
        <div className="landing-nav-inner">
          <div className="landing-logo">
            <span className="landing-logo-mark" aria-hidden="true">◆</span>
            CarbonTracker
          </div>
          <nav className="landing-nav-links">
            <a href="#features">Features</a>
            <a href="#how">How it works</a>
            <a href="#leaderboard">Leaderboard</a>
          </nav>
          <div className="landing-nav-actions">
            <button type="button" className="btn-ghost" onClick={() => navigate('/login')}>Log in</button>
            <button type="button" className="btn-solid" onClick={() => navigate('/login')}>Get started</button>
          </div>
        </div>
      </header>

      <main>
        <section className="hero">
          <div className="hero-copy">
            <p className="eyebrow">A running ledger of your day</p>
            <h1>
              Every trip, every meal,<br />
              every kilowatt <span className="hero-underline">counted</span>.
            </h1>
            <p className="hero-sub">
              CarbonTrail turns your daily choices into one honest number —
              logged in seconds, tracked by month, compared against everyone
              else doing the same.
            </p>
            <div className="hero-actions">
              <button type="button" className="btn-solid btn-lg" onClick={() => navigate('/login')}>
                Start your trail
              </button>
              <a href="#how" className="btn-text">See how it works →</a>
            </div>
            <p className="hero-proof">Joined by 10,000+ people logging their trail this month</p>
          </div>

          <div className="hero-trail" aria-hidden="true">
            <svg viewBox="0 0 420 360" className="trail-svg">
              <path
                d="M40 60 C 140 20, 200 100, 180 160 S 260 260, 220 300 S 340 320, 380 300"
                className="trail-path"
              />
              <g className="trail-node" transform="translate(40,60)">
                <circle r="30" />
                <foreignObject x="-14" y="-14" width="28" height="28">
                  <div className="trail-icon">{ICONS.transport}</div>
                </foreignObject>
              </g>
              <g className="trail-node" transform="translate(180,160)">
                <circle r="30" />
                <foreignObject x="-14" y="-14" width="28" height="28">
                  <div className="trail-icon">{ICONS.food}</div>
                </foreignObject>
              </g>
              <g className="trail-node" transform="translate(220,300)">
                <circle r="30" />
                <foreignObject x="-14" y="-14" width="28" height="28">
                  <div className="trail-icon">{ICONS.energy}</div>
                </foreignObject>
              </g>
              <g className="trail-node trail-node-you" transform="translate(380,300)">
                <circle r="34" />
                <text x="0" y="6" textAnchor="middle">you</text>
              </g>
            </svg>

            <div className="ledger-card">
              <p className="ledger-label">Today's running total</p>
              <p className="ledger-number">4.8<span>kg CO₂</span></p>
              <p className="ledger-delta">↓ 12% vs. your weekly average</p>
            </div>
          </div>
        </section>

        <section className="stat-strip">
          <div className="stat-strip-inner">
            <div className="stat-item" ref={usersRef}>
              <p className="stat-number">{usersCount.toLocaleString()}+</p>
              <p className="stat-label">People tracking</p>
            </div>
            <div className="stat-item" ref={co2Ref}>
              <p className="stat-number">{co2Count.toLocaleString()}</p>
              <p className="stat-label">kg CO₂ logged</p>
            </div>
            <div className="stat-item" ref={treesRef}>
              <p className="stat-number">{treesCount.toLocaleString()}</p>
              <p className="stat-label">Trees-equivalent saved</p>
            </div>
            <div className="stat-item">
              <p className="stat-number">3</p>
              <p className="stat-label">Minutes a day, that's it</p>
            </div>
          </div>
        </section>

        <section className="features" id="features">
          <p className="eyebrow center">What you log</p>
          <h2 className="section-title">Three habits, one honest number</h2>

          <div className="feature-grid">
            {FEATURES.map((f) => (
              <div className="feature-card" key={f.title}>
                <div className="feature-icon">{ICONS[f.icon]}</div>
                <h3>{f.title}</h3>
                <p>{f.desc}</p>
              </div>
            ))}
          </div>
        </section>

        <section className="how" id="how">
          <p className="eyebrow center">How it works</p>
          <h2 className="section-title">No spreadsheets. No guesswork.</h2>
          <div className="how-steps">
            <div className="how-step">
              <span className="how-index">1</span>
              <h3>Log a few numbers</h3>
              <p>Distance, quantity, units — whatever you did today, in under a minute.</p>
            </div>
            <div className="how-step">
              <span className="how-index">2</span>
              <h3>We do the math</h3>
              <p>Every entry converts to kg CO₂ automatically, no factors to look up.</p>
            </div>
            <div className="how-step">
              <span className="how-index">3</span>
              <h3>Watch your trail shrink</h3>
              <p>Monthly summaries and a leaderboard keep the habit honest.</p>
            </div>
          </div>
        </section>

        <section className="cta-band" id="leaderboard">
          <h2>Your trail starts with today's first entry.</h2>
          <button type="button" className="btn-solid btn-lg" onClick={() => navigate('/login')}>
            Get started free
          </button>
        </section>
      </main>

      <footer className="landing-footer">
        <div className="landing-logo">
          <span className="landing-logo-mark" aria-hidden="true">◆</span>
          CarbonTracker
        </div>
        <p>© {new Date().getFullYear()} CarbonTrail. Track less waste, more progress.</p>
      </footer>
    </div>
  );
}
