<template>
  <div class="fixed inset-0 -z-10 bg-gradient-to-b from-gray-900 via-gray-800 to-gray-700">
    <canvas ref="canvas" class="w-full h-full block"></canvas>
    <!-- optional overlay to dim or tint the background -->
    <div class="pointer-events-none absolute inset-0 mix-blend-overlay"></div>
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref } from 'vue'

// CONFIG - tweak these values as props if you like
const TRIANGLE_COUNT = 22 // how many triangles
const MIN_SIZE = 30
const MAX_SIZE = 120
const BORDER_WIDTH = 2.5
const SLOWNESS = 0.22 // multiplier for speed (smaller = slower)
const FPS_LIMIT = 60 // for consistent timing, kept at 60

const canvas = ref(null)
let ctx = null
let animationId = null
let dpr = 1
let width = 0
let height = 0

class Triangle {
  constructor() {
    this.reset(true)
  }

  reset(initial = false) {
    // randomize size and position
    this.size = randRange(MIN_SIZE, MAX_SIZE)
    this.h = (Math.sqrt(3) / 2) * this.size // height of equilateral triangle

    // place randomly but fully on screen
    this.x = randRange(this.size / 2, width - this.size / 2)
    this.y = randRange(this.h / 2, height - this.h / 2)

    // speed
    const angle = Math.random() * Math.PI * 2
    const speed = randRange(10, 70) * SLOWNESS
    this.vx = Math.cos(angle) * speed
    this.vy = Math.sin(angle) * speed

    // rotation
    this.rotation = Math.random() * Math.PI * 2
    this.rotationSpeed = (Math.random() - 0.5) * 0.001 * speed // slow rotation

    // style
    this.fillAlpha = randRange(0.02, 0.06) // very subtle fill
    this.stroke = '#0f172a' // Tailwind slate-900 / dark border
    this.strokeWidth = BORDER_WIDTH

    // subtle corner rounding factor for nicer look
    this.round = Math.min(6, this.size * 0.06)

    // keep initial triangles from teleporting by shrinking velocities
    if (initial) {
      this.vx *= 0.2
      this.vy *= 0.2
    }
  }

  step(dt) {
    // dt in seconds
    this.x += this.vx * dt
    this.y += this.vy * dt
    this.rotation += this.rotationSpeed * dt * 1000

    // collision with walls - bounce
    const halfW = this.size / 2
    const halfH = this.h / 2

    if (this.x - halfW <= 0) {
      this.x = halfW
      this.vx *= -1
    } else if (this.x + halfW >= width) {
      this.x = width - halfW
      this.vx *= -1
    }

    if (this.y - halfH <= 0) {
      this.y = halfH
      this.vy *= -1
    } else if (this.y + halfH >= height) {
      this.y = height - halfH
      this.vy *= -1
    }
  }

  draw(ctx) {
    ctx.save()
    ctx.translate(this.x, this.y)
    ctx.rotate(this.rotation)

    // draw equilateral triangle centered at 0,0 pointing up
    const s = this.size
    const h = this.h
    ctx.beginPath()
    ctx.moveTo(0, -h * 2 / 3)
    ctx.lineTo(-s / 2, h / 3)
    ctx.lineTo(s / 2, h / 3)
    ctx.closePath()

    // fill (very subtle)
    ctx.globalAlpha = this.fillAlpha
    ctx.fillStyle = '#020617'
    ctx.fill()

    // stroke
    ctx.globalAlpha = 1
    ctx.lineWidth = this.strokeWidth
    ctx.lineJoin = 'round'
    ctx.lineCap = 'round'
    ctx.strokeStyle = this.stroke
    ctx.stroke()

    ctx.restore()
  }
}

let triangles = []

function randRange(a, b) {
  return a + Math.random() * (b - a)
}

function resize() {
  if (!canvas.value) return
  dpr = window.devicePixelRatio || 1
  const rect = canvas.value.getBoundingClientRect()
  width = Math.max(1, Math.floor(rect.width))
  height = Math.max(1, Math.floor(rect.height))
  canvas.value.width = width * dpr
  canvas.value.height = height * dpr
  canvas.value.style.width = width + 'px'
  canvas.value.style.height = height + 'px'
  ctx.setTransform(dpr, 0, 0, dpr, 0, 0)
}

onMounted(() => {
  if (!canvas.value) return
  ctx = canvas.value.getContext('2d', { alpha: true })

  // initial size
  resize()

  // create triangles
  triangles = Array.from({ length: TRIANGLE_COUNT }, () => new Triangle())

  // animation loop with delta timing
  let last = performance.now()
  const frame = (now) => {
    const dt = Math.min(0.05, (now - last) / 1000) // clamp to avoid big jumps
    last = now

    // clear slowly with translucent rect to create soft trails (optional)
    ctx.clearRect(0, 0, width, height)

    // draw background overlay - subtle gradient using rect fill
    const g = ctx.createLinearGradient(0, 0, 0, height)
    g.addColorStop(0, '#0b1220')
    g.addColorStop(1, '#111827')
    ctx.fillStyle = g
    ctx.fillRect(0, 0, width, height)

    // step & draw triangles
    for (const t of triangles) {
      t.step(dt)
      t.draw(ctx)
    }

    animationId = requestAnimationFrame(frame)
  }
  animationId = requestAnimationFrame(frame)

  // resize listener
  const onResize = () => resize()
  window.addEventListener('resize', onResize)

  onBeforeUnmount(() => {
    cancelAnimationFrame(animationId)
    window.removeEventListener('resize', onResize)
  })
})
</script>

<style scoped>
/* ensure canvas covers the container - tailwind sets w-full h-full but keep as fallback */
canvas {
  display: block;
}
</style>
