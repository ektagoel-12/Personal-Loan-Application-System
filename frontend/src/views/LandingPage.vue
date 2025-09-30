<template>
  <div class="landing-page">
    <!-- Navigation Header -->
    <nav class="nav-header">
      <div class="nav-container">
        <div class="nav-brand">
          <img :src="logo" alt="LoanEase Logo" class="logo-img" />
          <span class="brand-text">LoanEase</span>
        </div>
        <div class="nav-links">
          <router-link to="/login-form" class="nav-link">Login</router-link>
          <router-link to="/registration-form" class="nav-link nav-cta"
            >Get Started</router-link
          >
        </div>
      </div>
    </nav>

    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-container">
        <div class="hero-content">
          <div class="hero-text">
            <h1 class="hero-title">
              Your Dream Loan,
              <span class="gradient-text">Just a Click Away</span>
            </h1>
            <p class="hero-description">
              Experience the fastest, most secure way to get personal loans in
              India. From application to approval in just 24 hours.
            </p>
            <div class="hero-features">
              <div class="feature-item">
                <CheckCircle class="feature-icon" />
                <span>Instant Approval</span>
              </div>
              <div class="feature-item">
                <Shield class="feature-icon" />
                <span>100% Secure</span>
              </div>
              <div class="feature-item">
                <Clock class="feature-icon" />
                <span>24/7 Support</span>
              </div>
            </div>
            <div class="hero-actions">
              <router-link to="/registration-form" class="btn-primary">
                Apply Now
                <ArrowRight class="btn-icon" />
              </router-link>
            </div>
          </div>
          <div class="hero-visual">
            <div class="money-flow-container">
              <div class="floating-money">
                <div
                  v-for="n in 12"
                  :key="n"
                  class="money-note"
                  :class="`note-${n}`"
                >
                  ₹
                </div>
              </div>
              <div class="central-icon">
                <div class="rupee-symbol">₹</div>
              </div>
              <div class="flow-lines">
                <div
                  v-for="n in 6"
                  :key="n"
                  class="flow-line"
                  :class="`line-${n}`"
                ></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Stats Section -->
    <section class="stats-section">
      <div class="stats-container">
        <div class="stat-item">
          <div class="stat-number">₹50K Cr+</div>
          <div class="stat-label">Loans Disbursed</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">10L+</div>
          <div class="stat-label">Happy Customers</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">24 Hours</div>
          <div class="stat-label">Quick Approval</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">8.99%</div>
          <div class="stat-label">Starting Interest</div>
        </div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-section">
      <div class="features-container">
        <div class="section-header">
          <h2 class="section-title">Why Choose LoanEase?</h2>
          <p class="section-description">
            We make personal loans simple, fast, and transparent
          </p>
        </div>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon-wrapper">
              <Zap class="feature-icon-large" />
            </div>
            <h3 class="feature-title">Lightning Fast</h3>
            <p class="feature-description">
              Get loan approval in just 24 hours with minimal documentation
            </p>
          </div>
          <div class="feature-card">
            <div class="feature-icon-wrapper">
              <Calculator class="feature-icon-large" />
            </div>
            <h3 class="feature-title">EMI Calculator</h3>
            <p class="feature-description">
              Plan your finances with our interactive EMI calculator
            </p>
          </div>
          <div class="feature-card">
            <div class="feature-icon-wrapper">
              <HeadphonesIcon class="feature-icon-large" />
            </div>
            <h3 class="feature-title">24/7 Support</h3>
            <p class="feature-description">
              Round-the-clock customer support for all your queries
            </p>
          </div>
          <div class="feature-card">
            <div class="feature-icon-wrapper">
              <TrendingUp class="feature-icon-large" />
            </div>
            <h3 class="feature-title">Competitive Rates</h3>
            <p class="feature-description">
              Best-in-class interest rates starting from 8.99% per annum
            </p>
          </div>
        </div>
      </div>
    </section>

    <!-- How It Works -->
    <section class="process-section">
      <div class="process-container">
        <div class="section-header">
          <h2 class="section-title">How It Works</h2>
          <p class="section-description">
            Simple steps to get your loan approved
          </p>
        </div>
        <div class="process-steps">
          <div class="step-item">
            <div class="step-number">1</div>
            <div class="step-content">
              <h3 class="step-title">Apply Online</h3>
              <p class="step-description">
                Fill out our simple online application form
              </p>
            </div>
          </div>
          <div class="step-connector"></div>
          <div class="step-item">
            <div class="step-number">2</div>
            <div class="step-content">
              <h3 class="step-title">Document Verification</h3>
              <p class="step-description">
                Upload required documents for verification
              </p>
            </div>
          </div>
          <div class="step-connector"></div>
          <div class="step-item">
            <div class="step-number">3</div>
            <div class="step-content">
              <h3 class="step-title">Get Approved</h3>
              <p class="step-description">
                Receive approval and loan amount in 24 hours
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta-section">
      <div class="cta-container">
        <div class="cta-content">
          <h2 class="cta-title">Ready to Get Your Loan?</h2>
          <p class="cta-description">
            Join thousands of satisfied customers who chose LoanEase for their
            financial needs
          </p>
          <div class="cta-actions">
            <router-link to="/login-form" class="btn-primary-large">
              Apply for Loan
              <ArrowRight class="btn-icon" />
            </router-link>
          </div>
        </div>
      </div>
    </section>

    <!-- Footer -->
    <footer class="footer">
      <div class="footer-container">
        <div class="footer-content">

          <!-- Footer Links -->
          <div class="footer-links">
            <!-- Products Section -->
            <div class="footer-section">
              <h4 class="footer-title">Products</h4>
              <a href="/login-form" class="footer-link">Personal Loans</a>
              <a href="/login-form" class="footer-link">EMI Calculator</a>
              <a href="/login-form" class="footer-link">Loan Status</a>
              <a href="/login-form" class="footer-link">Support Tickets</a>
            </div>

            <!-- Admin Section -->
            <div class="footer-section">
              <h4 class="footer-title">Admin Tools</h4>
              <a href="/login-form" class="footer-link">Review Applications</a>
              <a href="/login-form" class="footer-link">Manage Support</a>
              <a href="/login-form" class="footer-link">Admin Dashboard</a>
            </div>

            <!-- Company Section -->
            <div class="footer-section">
              <h4 class="footer-title">Company</h4>
              <a href="/" class="footer-link">About Us</a>
              <a href="mailto:loanease@gmail.com" class="footer-link"
                >Contact Us</a
              >
            </div>
          </div>
        </div>

        <!-- Footer Bottom -->
        <div class="footer-bottom">
          <p>&copy; 2024 LoanEase. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import {
  CheckCircle,
  Shield,
  Clock,
  ArrowRight,
  Zap,
  Calculator,
  HeadphonesIcon,
  TrendingUp,
} from "lucide-vue-next";
import logo from "../assets/LoanEase_logo.png";
</script>

<style scoped>
.landing-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8f9ff 0%, #e8ebff 100%);
}

.nav-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(147, 112, 219, 0.1);
  z-index: 100;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 70px;
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: -40px; /* move left by 10px */
}

.brand-icon {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #9370db, #ba87eb);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 18px;
}

.brand-text {
  font-size: 24px;
  font-weight: 700;
  color: white;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 32px;
}

.nav-link {
  text-decoration: none;
  color: #64748b;
  font-weight: 500;
  transition: color 0.2s ease;
}

.nav-link:hover {
  color: #9370db;
}

.nav-cta {
  background: linear-gradient(135deg, #9370db, #ba87eb);
  color: white !important;
  padding: 10px 20px;
  border-radius: 8px;
  transition: transform 0.2s ease;
}

.nav-cta:hover {
  transform: translateY(-1px);
  color: white !important;
}

.hero-section {
  padding: 120px 0 80px;
}

.hero-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.hero-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 80px;
  align-items: center;
}

.hero-title {
  font-size: 56px;
  font-weight: 700;
  line-height: 1.1;
  color: #2d1b4e;
  margin-bottom: 24px;
}

.gradient-text {
  background: linear-gradient(135deg, #9370db, #ba87eb);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-description {
  font-size: 20px;
  color: #64748b;
  line-height: 1.6;
  margin-bottom: 32px;
}

.hero-features {
  display: flex;
  gap: 32px;
  margin-bottom: 40px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #64748b;
  font-weight: 500;
}

.feature-icon {
  width: 20px;
  height: 20px;
  color: #9370db;
}

.hero-actions {
  display: flex;
  gap: 20px;
}

.btn-primary {
  background: linear-gradient(135deg, #9370db, #ba87eb);
  color: white;
  padding: 16px 32px;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(147, 112, 219, 0.3);
}

.btn-secondary {
  background: white;
  color: #9370db;
  padding: 16px 32px;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 600;
  border: 2px solid #9370db;
  transition: all 0.2s ease;
}

.btn-secondary:hover {
  background: #9370db;
  color: white;
}

.btn-icon {
  width: 18px;
  height: 18px;
}

.hero-visual {
  display: flex;
  justify-content: center;
  align-items: center;
}

.money-flow-container {
  position: relative;
  width: 400px;
  height: 400px;
}

.central-icon {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 120px;
  height: 120px;
  background: linear-gradient(135deg, #9370db, #ba87eb);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 20px 60px rgba(147, 112, 219, 0.3);
}

.rupee-symbol {
  font-size: 48px;
  font-weight: bold;
  color: white;
}

.floating-money {
  position: absolute;
  width: 100%;
  height: 100%;
}

.money-note {
  position: absolute;
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #f0f9ff, #e0f2fe);
  border: 2px solid #9370db;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
  color: #9370db;
  animation: float 3s ease-in-out infinite;
}

.note-1 {
  top: 10%;
  left: 20%;
  animation-delay: 0s;
}
.note-2 {
  top: 15%;
  left: 70%;
  animation-delay: 0.5s;
}
.note-3 {
  top: 30%;
  left: 10%;
  animation-delay: 1s;
}
.note-4 {
  top: 35%;
  left: 85%;
  animation-delay: 1.5s;
}
.note-5 {
  top: 55%;
  left: 5%;
  animation-delay: 2s;
}
.note-6 {
  top: 60%;
  left: 80%;
  animation-delay: 2.5s;
}
.note-7 {
  top: 75%;
  left: 25%;
  animation-delay: 0.3s;
}
.note-8 {
  top: 80%;
  left: 65%;
  animation-delay: 0.8s;
}
.note-9 {
  top: 45%;
  left: 90%;
  animation-delay: 1.3s;
}
.note-10 {
  top: 25%;
  left: 90%;
  animation-delay: 1.8s;
}
.note-11 {
  top: 70%;
  left: 10%;
  animation-delay: 0.2s;
}
.note-12 {
  top: 90%;
  left: 50%;
  animation-delay: 1.2s;
}

@keyframes float {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

.flow-lines {
  position: absolute;
  width: 100%;
  height: 100%;
}

.flow-line {
  position: absolute;
  width: 2px;
  height: 60px;
  background: linear-gradient(to bottom, transparent, #9370db, transparent);
  border-radius: 1px;
  animation: flow 2s linear infinite;
}

.line-1 {
  top: 20%;
  left: 25%;
  animation-delay: 0s;
}
.line-2 {
  top: 30%;
  right: 25%;
  animation-delay: 0.3s;
}
.line-3 {
  bottom: 30%;
  left: 30%;
  animation-delay: 0.6s;
}
.line-4 {
  bottom: 20%;
  right: 30%;
  animation-delay: 0.9s;
}
.line-5 {
  top: 50%;
  left: 15%;
  animation-delay: 1.2s;
}
.line-6 {
  top: 50%;
  right: 15%;
  animation-delay: 1.5s;
}

@keyframes flow {
  0% {
    opacity: 0;
    transform: translateY(-10px);
  }
  50% {
    opacity: 1;
  }
  100% {
    opacity: 0;
    transform: translateY(10px);
  }
}

.stats-section {
  padding: 80px 0;
  background: white;
}
.logo-img {
  height: 140px; /* adjust as needed */
  width: auto;
  object-fit: contain;
}

.stats-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 40px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 36px;
  font-weight: 700;
  color: #9370db;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 16px;
  color: #64748b;
  font-weight: 500;
}

.features-section {
  padding: 100px 0;
}

.features-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.section-header {
  text-align: center;
  margin-bottom: 80px;
}

.section-title {
  font-size: 42px;
  font-weight: 700;
  color: #2d1b4e;
  margin-bottom: 16px;
}

.section-description {
  font-size: 20px;
  color: #64748b;
  max-width: 600px;
  margin: 0 auto;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 40px;
}

.feature-card {
  background: white;
  padding: 40px;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.12);
}

.feature-icon-wrapper {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #f0f9ff, #e0f2fe);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 24px;
}

.feature-icon-large {
  width: 32px;
  height: 32px;
  color: #9370db;
}

.feature-title {
  font-size: 24px;
  font-weight: 600;
  color: #2d1b4e;
  margin-bottom: 12px;
}

.feature-description {
  font-size: 16px;
  color: #64748b;
  line-height: 1.6;
}

.process-section {
  padding: 100px 0;
  background: white;
}

.process-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.process-steps {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40px;
}

.step-item {
  display: flex;
  align-items: center;
  gap: 20px;
  max-width: 300px;
}

.step-number {
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #9370db, #ba87eb);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  font-weight: bold;
  flex-shrink: 0;
}

.step-title {
  font-size: 20px;
  font-weight: 600;
  color: #2d1b4e;
  margin-bottom: 8px;
}

.step-description {
  font-size: 14px;
  color: #64748b;
  line-height: 1.5;
}

.step-connector {
  width: 80px;
  height: 2px;
  background: linear-gradient(to right, #9370db, #ba87eb);
  border-radius: 1px;
}

.cta-section {
  padding: 100px 0;
  background: linear-gradient(135deg, #9370db, #ba87eb);
}

.cta-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.cta-content {
  text-align: center;
  color: white;
}

.cta-title {
  font-size: 42px;
  font-weight: 700;
  margin-bottom: 16px;
}

.cta-description {
  font-size: 20px;
  opacity: 0.9;
  margin-bottom: 40px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.btn-primary-large {
  background: white;
  color: #9370db;
  padding: 20px 40px;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 600;
  font-size: 18px;
  display: inline-flex;
  align-items: center;
  gap: 12px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.btn-primary-large:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.2);
}

.footer {
  background: #1e293b;
  color: white;
  padding: 60px 0 30px;
  text-align: center;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
}

.footer-content {
  display: block;          /* was a 2-col grid; remove it */
  margin-bottom: 40px;
  width: 100%;
}

.footer-links {
  display: grid;
  grid-template-columns: repeat(3, minmax(220px, 1fr)); /* 3 equal columns */
  gap: 64px;               /* spacing between columns */
  width: 100%;             /* stretch across the container */
  text-align: left;
}

.footer-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 16px;
  color: white;
}

.footer-section {
  text-align: center;
}

.footer-link {
  display: block;
  color: #94a3b8;
  text-decoration: none;
  margin-bottom: 8px;
  transition: color 0.2s ease;
}

.footer-link:hover {
  color: #9370db;
}

.footer-bottom {
  padding-top: 30px;
  border-top: 1px solid #334155;
  text-align: center;
  color: #94a3b8;
}

@media (max-width: 768px) {
  .hero-content {
    grid-template-columns: 1fr;
    gap: 40px;
    text-align: center;
  }

  .hero-title {
    font-size: 36px;
  }

  .features-grid {
    grid-template-columns: 1fr;
  }

  .stats-container {
    grid-template-columns: repeat(2, 1fr);
    gap: 30px;
  }

  .process-steps {
    flex-direction: column;
    gap: 30px;
  }

  .step-connector {
    width: 2px;
    height: 40px;
  }

  .nav-links {
    gap: 16px;
  }

  .hero-features {
    flex-direction: column;
    gap: 16px;
  }

  .hero-actions {
    flex-direction: column;
  }
}
</style>
