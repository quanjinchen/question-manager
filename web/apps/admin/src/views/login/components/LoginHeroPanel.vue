<template>
  <div class="LoginHeroPanel-root">
    <div class="security-auth-animation">
      <svg
        class="hexagon-grid"
        viewBox="0 0 100 100"
        preserveAspectRatio="xMidYMid slice"
        xmlns="http://www.w3.org/2000/svg"
      >
        <defs>
          <pattern
            id="hexPattern"
            x="0"
            y="0"
            width="15"
            height="13"
            patternUnits="userSpaceOnUse"
          >
            <polygon
              points="7.5,0 13,3.75 13,9.25 7.5,13 2,9.25 2,3.75"
              fill="none"
              stroke="rgba(59, 130, 246, 0.12)"
              stroke-width="0.3"
            />
          </pattern>
        </defs>
        <rect width="100" height="100" fill="url(#hexPattern)" />
      </svg>

      <svg
        class="security-mesh"
        viewBox="0 0 200 240"
        xmlns="http://www.w3.org/2000/svg"
      >
        <g
          class="mesh-lines"
          stroke="#3b82f6"
          stroke-width="1.5"
          fill="none"
          opacity="0.5"
        >
          <path d="M 40 60 Q 100 55 160 60" class="mesh-line" />
          <path d="M 35 80 Q 100 75 165 80" class="mesh-line" />
          <path d="M 30 100 Q 100 95 170 100" class="mesh-line" />
          <path d="M 30 120 Q 100 115 170 120" class="mesh-line" />
          <path d="M 35 140 Q 100 135 165 140" class="mesh-line" />
          <path d="M 40 160 Q 100 155 160 160" class="mesh-line" />
          <path d="M 50 180 Q 100 175 150 180" class="mesh-line" />

          <path d="M 100 40 L 100 200" class="mesh-line" />
          <path d="M 70 50 Q 70 120 75 190" class="mesh-line" />
          <path d="M 130 50 Q 130 120 125 190" class="mesh-line" />
          <path d="M 50 70 Q 45 120 55 180" class="mesh-line" />
          <path d="M 150 70 Q 155 120 145 180" class="mesh-line" />
        </g>

        <g class="signal-points">
          <circle cx="75" cy="100" r="3" fill="#3b82f6" class="point" />
          <circle cx="125" cy="100" r="3" fill="#3b82f6" class="point" />
          <circle cx="100" cy="120" r="3" fill="#3b82f6" class="point" />
          <circle cx="85" cy="150" r="3" fill="#3b82f6" class="point" />
          <circle cx="115" cy="150" r="3" fill="#3b82f6" class="point" />
          <circle cx="100" cy="80" r="3" fill="#3b82f6" class="point" />
          <circle cx="60" cy="110" r="3" fill="#3b82f6" class="point" />
          <circle cx="140" cy="110" r="3" fill="#3b82f6" class="point" />
        </g>
      </svg>

      <svg
        class="scan-frame"
        viewBox="0 0 320 400"
        xmlns="http://www.w3.org/2000/svg"
      >
        <g
          stroke="#3b82f6"
          stroke-width="2.5"
          fill="none"
          class="corner-frame"
        >
          <path d="M 60 60 L 100 60 M 60 60 L 60 100" />
          <path d="M 260 60 L 220 60 M 260 60 L 260 100" />
          <path d="M 60 340 L 100 340 M 60 340 L 60 300" />
          <path d="M 260 340 L 220 340 M 260 340 L 260 300" />
        </g>

        <line
          x1="60"
          y1="200"
          x2="260"
          y2="200"
          stroke="#3b82f6"
          stroke-width="2"
          opacity="0.8"
          class="scan-line-horizontal"
        />
      </svg>

      <canvas ref="canvasRef" class="particle-canvas"></canvas>
    </div>

    <div class="LoginHeroPanel-content">
      <h1 class="brand-title">{{ adminStore.title }}</h1>
      <p class="brand-subtitle">身份核验 · 安全认证</p>
    </div>
  </div>
</template>

<script setup lang="ts" name="LoginHeroPanel">
import { onMounted, onUnmounted, ref } from "vue";
import { useAdminStore } from "@/stores";

interface Particle {
  x: number;
  y: number;
  vx: number;
  vy: number;
  radius: number;
}

const PARTICLE_COUNT = 30;
const PARTICLE_COLOR = "rgba(139, 92, 246, 0.2)";
const PARTICLE_SPEED = 0.3;

const adminStore = useAdminStore();
const canvasRef = ref<HTMLCanvasElement>();

let animationId = 0;
let particles: Particle[] = [];

function createParticles(width: number, height: number) {
  return Array.from({ length: PARTICLE_COUNT }, () => ({
    x: Math.random() * width,
    y: Math.random() * height,
    vx: (Math.random() - 0.5) * PARTICLE_SPEED,
    vy: (Math.random() - 0.5) * PARTICLE_SPEED,
    radius: Math.random() * 2 + 1,
  }));
}

function drawParticles() {
  const canvas = canvasRef.value;
  const context = canvas?.getContext("2d");
  if (!canvas || !context) {
    return;
  }

  context.clearRect(0, 0, canvas.width, canvas.height);

  particles.forEach((particle) => {
    particle.x += particle.vx;
    particle.y += particle.vy;

    if (particle.x < 0 || particle.x > canvas.width) {
      particle.vx *= -1;
    }
    if (particle.y < 0 || particle.y > canvas.height) {
      particle.vy *= -1;
    }

    context.beginPath();
    context.arc(particle.x, particle.y, particle.radius, 0, Math.PI * 2);
    context.fillStyle = PARTICLE_COLOR;
    context.fill();
  });

  animationId = window.requestAnimationFrame(drawParticles);
}

function initParticles() {
  const canvas = canvasRef.value;
  const context = canvas?.getContext("2d");
  if (!canvas || !context) {
    return;
  }

  canvas.width = canvas.offsetWidth;
  canvas.height = canvas.offsetHeight;
  particles = createParticles(canvas.width, canvas.height);

  window.cancelAnimationFrame(animationId);
  drawParticles();
}

onMounted(() => {
  initParticles();
  window.addEventListener("resize", initParticles);
});

onUnmounted(() => {
  window.cancelAnimationFrame(animationId);
  window.removeEventListener("resize", initParticles);
});
</script>

<style scoped lang="scss">
.LoginHeroPanel-root {
  width: 100%;
  height: 100%;
  position: relative;
  background: linear-gradient(135deg, #e0f2fe 0%, #dbeafe 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.security-auth-animation {
  position: relative;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.particle-canvas {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.hexagon-grid {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.security-mesh {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 280px;
  height: 340px;
  z-index: 3;
  transform: translate(-50%, -50%);

  .mesh-line {
    animation: meshPulse 3s ease-in-out infinite;
  }

  .point {
    animation: pointGlow 2s ease-in-out infinite;
  }
}

.scan-frame {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 400px;
  height: 480px;
  z-index: 4;
  transform: translate(-50%, -50%);

  .corner-frame {
    animation: framePulse 2s ease-in-out infinite;
  }

  .scan-line-horizontal {
    animation: scanHorizontal 4s linear infinite;
  }
}

.LoginHeroPanel-content {
  position: absolute;
  bottom: 60px;
  z-index: 5;
  padding: 20px;
  text-align: center;

  @media (max-width: 768px) {
    bottom: 30px;
  }
}

.brand-title {
  margin: 0 0 8px;
  font-size: 42px;
  font-weight: 700;
  color: #1e40af;

  @media (max-width: 768px) {
    font-size: 28px;
  }
}

.brand-subtitle {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0.05em;
  color: #3b82f6;

  @media (max-width: 768px) {
    font-size: 14px;
  }
}

@keyframes meshPulse {
  0%,
  100% {
    stroke-opacity: 0.3;
  }
  50% {
    stroke-opacity: 0.7;
  }
}

@keyframes pointGlow {
  0%,
  100% {
    opacity: 0.4;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.3);
  }
}

@keyframes framePulse {
  0%,
  100% {
    opacity: 0.4;
  }
  50% {
    opacity: 0.9;
  }
}

@keyframes scanHorizontal {
  0% {
    transform: translateY(-140px);
    opacity: 0;
  }
  10% {
    opacity: 0.8;
  }
  90% {
    opacity: 0.8;
  }
  100% {
    transform: translateY(140px);
    opacity: 0;
  }
}
</style>
