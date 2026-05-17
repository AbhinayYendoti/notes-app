# Frontend Module PRD — Notes App UI
> **Design System: WIRED Editorial Design Language**

---

## GLOBAL ENGINEERING RULES APPLY (see master_prd.md)

---

## Overview

The Notes App frontend is a single-page HTML application built in the **WIRED editorial design language** — a strict black-and-white magazine aesthetic. No gradients, no soft shadows, no chromatic accents in the UI. The surface reads as a printed magazine ported to a browser: white canvas, black ink, hairline dividers, and square geometry everywhere.

The only colour deviation is the inline link blue (`#057dbc`) used sparingly inside article/note body text.

---

## Tech Stack (Frontend)

```
Single HTML file (notes-app-ui.html)
Vanilla JS (no framework — keep it interview-simple)
CSS variables matching the WIRED token system
Google Fonts: Playfair Display (WiredDisplay substitute), Lora (BreveText substitute), Manrope (Apercu substitute)
Fetch API for all backend calls
```

---

## Design Tokens (CSS Variables)

```css
/* WIRED Editorial Design System */
:root {
  /* Colors */
  --color-primary:     #000000;   /* Ink Black — wordmark, CTAs, footer fill */
  --color-canvas:      #ffffff;   /* Page background */
  --color-canvas-soft: #f5f5f5;   /* Hover states, search rows */
  --color-hairline:    #e0e0e0;   /* 1px story-row dividers */
  --color-ink:         #000000;   /* Headlines, body */
  --color-ink-soft:    #1a1a1a;   /* Caption-strong, footer link emphasis */
  --color-body:        #757575;   /* Bylines, timestamps, metadata */
  --color-link:        #057dbc;   /* Inline body links ONLY — never on buttons */
  --color-on-primary:  #ffffff;   /* Text on black backgrounds */

  /* Typography — Font Families */
  --font-display:  'Playfair Display', Georgia, serif;    /* WiredDisplay substitute */
  --font-body:     'Lora', 'Source Serif Pro', serif;     /* BreveText substitute */
  --font-sans:     'Manrope', 'Inter', sans-serif;        /* Apercu substitute */

  /* Typography — Display Scale */
  --text-display-hero: 64px;   /* Cover story headline */
  --text-display-lg:   48px;   /* Major section headlines */
  --text-display-md:   32px;   /* Story card headlines */
  --text-display-sm:   26px;   /* Sub-display headings */
  --text-display-xs:   20px;   /* Sans micro-headings */

  /* Typography — Body Scale */
  --text-body-serif-lg: 19px;  /* Article lead paragraph */
  --text-body-serif-md: 16px;  /* Default article body */
  --text-body-md:       17px;  /* Sans nav / metadata */
  --text-body-sm:       14px;  /* Secondary sans body */
  --text-byline:        12.73px; /* Article byline */
  --text-caption:       12px;  /* Fine print */
  --text-button:        16px;  /* Button label */

  /* Spacing — 4px base */
  --space-xxs: 2px;
  --space-xs:  4px;
  --space-sm:  8px;
  --space-md:  12px;
  --space-lg:  16px;
  --space-xl:  20px;
  --space-2xl: 24px;
  --space-3xl: 32px;
  --space-4xl: 48px;

  /* Border Radius — WIRED IS SQUARE */
  --rounded-none: 0px;       /* EVERY button, input, card — non-negotiable */
  --rounded-full: 9999px;    /* ONLY for circular avatar / share icons */

  /* Elevation — hairlines ONLY, no shadows */
  --border-hairline: 1px solid var(--color-hairline);
  --border-ink:      1px solid var(--color-ink);
  --border-ink-heavy: 2px solid var(--color-ink);

  /* Container */
  --container-max: 1400px;
}
```

---

## Layout Structure

```
┌─────────────────────────────────────────────────────┐
│  MASTHEAD BAND  (thin, wordmark centred)             │
├─────────────────────────────────────────────────────┤
│  NAV BAR  (hamburger | NOTES | Subscribe)            │
├─────────────────────────────────────────────────────┤
│                                                      │
│  MAIN CONTENT AREA  (switches based on auth state)   │
│                                                      │
│  ┌─ Unauthenticated ───────────────────────────┐    │
│  │  Auth form card (Login / Register)           │    │
│  └─────────────────────────────────────────────┘    │
│                                                      │
│  ┌─ Authenticated ────────────────────────────────┐ │
│  │  HERO BAND — Create New Note CTA               │ │
│  │                                                │ │
│  │  MAGAZINE GRID:                                │ │
│  │  ┌──────────────────┐  ┌──────┐  ┌──────┐    │ │
│  │  │  story-card-large│  │ card │  │ card │    │ │
│  │  └──────────────────┘  └──────┘  └──────┘    │ │
│  │                                                │ │
│  │  STORY ROW STACK (all notes, hairline dividers)│ │
│  │  ─────────────────────────────────────────     │ │
│  │  📌 Pinned Note Title          byline / date   │ │
│  │  ─────────────────────────────────────────     │ │
│  │  Regular Note Title            byline / date   │ │
│  └────────────────────────────────────────────────┘ │
│                                                      │
├─────────────────────────────────────────────────────┤
│  FOOTER  (black band, text columns, wordmark)        │
└─────────────────────────────────────────────────────┘
```

---

## Screens / Views

### 1. Landing / Auth Screen (unauthenticated)

**Layout:** Centered auth card on white canvas.

```
Components:
  - masthead-band (wordmark: "NOTES")
  - nav-bar (no user links, just branding)
  - ex-auth-form-card:
      - Tab toggle: "Sign In" | "Register" (button-outline active state)
      - text-input: email (square, 1px ink border)
      - text-input: password
      - button-primary: "SIGN IN" / "CREATE ACCOUNT"
      - Error state: inline below input, ink color, Apercu 14px
  - footer
```

**WIRED specifics:**
- Card has NO border-radius (rounded-none)
- No drop-shadow — use border-hairline if card needs definition
- Form labels in Apercu 14px/700 ALL CAPS (category-eyebrow style)
- Error messages in ink-soft color, Apercu 14px

---

### 2. Notes Dashboard (authenticated)

**Layout:** Full magazine grid

```
Components:
  - masthead-band
  - nav-bar (with "NEW NOTE" button-primary right side)
  - hero-band: "YOUR NOTES" in display-hero (Playfair 64px)
  - Magazine grid section:
      - story-card-large: most recent/pinned note (display-md headline)
      - 2x story-card: next two notes (display-sm headline, 4:3 ratio area)
  - Story row stack:
      - story-row per remaining note (hairline bottom border)
      - 📌 pin icon for pinned notes (visible badge)
      - Actions: Edit (button-outline xs), Delete (text link, ink-soft)
      - Share icon: button-icon-circular
  - Empty state: centered, byline typography, "No notes yet. Create your first." + button-primary
  - footer
```

**WIRED specifics:**
- Grid uses CSS Grid, max 1400px container
- Pinned notes appear first — badge: small black square with "PINNED" in Apercu 10px/700
- story-card hover: no shadow — background shifts to canvas-soft (#f5f5f5)
- category-eyebrow above each card headline: "TECHNOLOGY" / "PERSONAL" derived from first word of content, or just "NOTE"

---

### 3. Note Editor (create / edit)

**Layout:** Wide editorial article column

```
Components:
  - masthead-band
  - nav-bar (back arrow left, "SAVE" button-primary right)
  - Article-style editor column (max 720px centred):
      - category-eyebrow: "NEW NOTE" / "EDITING"
      - text-input (large): Title — display-md style input (Playfair 32px, no border except bottom hairline)
      - textarea: Content — body-serif-md (Lora 16px, line-height 1.5, no border except bottom hairline)
      - byline-row: "Last saved: [time]" in byline typography
      - Share section:
          - hairline-divider
          - "SHARE WITH" label (category-eyebrow)
          - text-input: email
          - button-outline: "SHARE"
  - footer
```

**WIRED specifics:**
- Title input looks like a display headline — large Playfair Display, minimal chrome
- Content textarea is frameless with only a bottom hairline on focus
- Save button: always visible in nav-bar, ink black, square
- Autosave indicator: "Saved" in Apercu 12px, ink-soft, fades after 2s

---

### 4. Note Detail View

**Layout:** Long-form article layout

```
Components:
  - masthead-band
  - nav-bar (back link left, edit + share CTAs right)
  - Article layout (max 720px centred):
      - category-eyebrow: "NOTE"
      - h1: Note title in display-hero (Playfair 64px → 40px mobile)
      - byline-row: created date, author (circular avatar 28px)
      - hairline-divider
      - Article body in body-serif-md (Lora 16px / line-height 1.5)
      - Share band:
          - hairline-divider
          - "SHARE" label + button-icon-circular (social-share style)
      - 📌 Pin toggle: button-outline "PIN NOTE" / "UNPIN"
      - Delete: text link in ink-soft (14px) "Delete note"
  - footer
```

---

## Component Specifications

### button-primary
```css
background: var(--color-primary);        /* #000000 */
color: var(--color-on-primary);          /* #ffffff */
font-family: var(--font-sans);           /* Manrope */
font-size: var(--text-button);           /* 16px */
font-weight: 700;
letter-spacing: 0.3px;
padding: var(--space-md) var(--space-xl); /* 12px 20px */
border-radius: var(--rounded-none);      /* 0px — NON-NEGOTIABLE */
border: none;
cursor: pointer;
text-transform: uppercase;
/* Hover: background #1a1a1a — NO shadow, just slight darken */
```

### button-outline
```css
background: var(--color-canvas);
color: var(--color-ink);
border: var(--border-ink);              /* 1px solid #000 */
/* Same typography, padding, rounded-none as button-primary */
/* Hover: background canvas-soft (#f5f5f5) */
```

### text-input
```css
background: var(--color-canvas);
color: var(--color-ink);
border: var(--border-ink);              /* 1px solid #000 */
border-radius: var(--rounded-none);     /* 0px */
font-family: var(--font-sans);
font-size: var(--text-body-md);         /* 17px */
padding: var(--space-md) var(--space-lg); /* 12px 16px */
/* Focus: border 2px solid ink — NO glow, NO color ring */
```

### story-card-large
```css
background: var(--color-canvas);
border: none;                           /* No border — lives on canvas */
border-radius: var(--rounded-none);
padding: var(--space-lg);
/* Headline: font-family display, font-size display-md (32px) */
/* Eyebrow: Apercu 14px/700 uppercase */
/* Hover: background canvas-soft — NO shadow */
```

### story-row
```css
border-bottom: var(--border-hairline);  /* 1px solid #e0e0e0 */
border-radius: var(--rounded-none);
padding: var(--space-lg) 0;
font-family: var(--font-sans);
font-size: var(--text-body-md);
font-weight: 700;
/* Hover: background canvas-soft */
```

### masthead-band
```css
background: var(--color-canvas);
border-bottom: var(--border-hairline);
padding: var(--space-md) var(--space-xl);
text-align: center;
/* Wordmark: font-display, font-size 28px, font-weight 400 */
/* No other decoration — just the wordmark, centred */
```

### nav-bar
```css
background: var(--color-canvas);
border-bottom: var(--border-hairline);
padding: var(--space-md) var(--space-xl);
display: flex;
align-items: center;
justify-content: space-between;
/* Links: Apercu 14px/700 */
/* Subscribe/New Note: button-primary */
```

### footer
```css
background: var(--color-primary);       /* #000000 */
color: var(--color-on-primary);         /* #ffffff */
padding: var(--space-4xl) var(--space-xl);
font-family: var(--font-sans);
font-size: var(--text-body-sm);         /* 14px */
/* Column eyebrows: 14px/700 uppercase */
/* Links: color #ffffff, no underline, hover underline */
/* Wordmark repeats at bottom centred */
```

### byline-row
```css
font-family: var(--font-body);          /* Lora */
font-size: var(--text-byline);          /* 12.73px */
font-weight: 700;
line-height: 2.2;                       /* Signature editorial breathing */
color: var(--color-body);              /* #757575 */
/* Avatar: 28px circle (rounded-full) */
```

### hairline-divider
```css
border: none;
border-top: var(--border-hairline);     /* 1px solid #e0e0e0 */
margin: var(--space-lg) 0;
```

---

## State Management (Vanilla JS)

```javascript
// App state object
const state = {
  token: null,           // JWT token (memory only — no localStorage)
  user: null,            // { email }
  notes: [],             // List<NoteResponse>
  currentNote: null,     // NoteResponse | null
  view: 'auth',          // 'auth' | 'dashboard' | 'editor' | 'detail'
  loading: false,
  error: null
};

// API base URL — change before deploying
const API_BASE = 'https://your-app.render.com';
```

**View routing:** Hash-based (`#dashboard`, `#note/uuid`, `#new`, `#login`)

---

## API Integration

```javascript
// All API calls follow this pattern
async function apiCall(method, path, body = null) {
  const headers = { 'Content-Type': 'application/json' };
  if (state.token) headers['Authorization'] = `Bearer ${state.token}`;

  const res = await fetch(`${API_BASE}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : null
  });

  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: 'Request failed' }));
    throw new Error(err.message || 'Request failed');
  }

  return res.status === 204 ? null : res.json();
}
```

---

## WIRED Design Rules — DO NOT VIOLATE

```
✅ DO:
  - Use Playfair Display (weight 400) for ALL display headlines
  - Use rounded-none (0px) on EVERY button, input, card
  - Use hairline dividers (1px #e0e0e0) between story rows — the brand's ONLY elevation
  - Use black (#000000) for wordmark, CTAs, footer fill
  - Use Apercu/Manrope for nav links, button labels, eyebrows, metadata
  - Use Lora for note body text in detail/editor views
  - Show pinned notes first with a small "PINNED" badge

❌ DO NOT:
  - Add gradients anywhere
  - Round button or input corners
  - Add drop-shadows on cards
  - Use the link blue (#057dbc) on buttons or nav — inline note content ONLY
  - Use bold weight (700) on display headlines — elegance comes from the typeface
  - Center-align body text or story rows
  - Add decorative background shapes, blobs, or patterns
```

---

## Responsive Behaviour

```
Mobile (< 768px):
  - Hero headline: 64px → 40px
  - Magazine grid: all cards 1-up (stack)
  - Nav: hamburger left, wordmark centre
  - Editor: full width, larger tap targets

Tablet (768–1023px):
  - 2-up secondary story grid
  - Full nav visible

Desktop (≥ 1024px):
  - Full magazine grid: 1 large + 2-up + story rows
  - Container max 1400px
```

---

## File to Deliver

Single file: `notes-app-ui.html`
All CSS inline in `<style>` tag.
All JS inline in `<script>` tag.
Fonts loaded via Google Fonts CDN.
API_BASE constant at top of script — easy to update before deploy.

---

## Agent Prompt for Frontend

```
You are the Frontend Specialist Agent for the Notes App project.

READ THESE FILES FIRST:
1. docs/01_master_prd/master_prd.md
2. docs/02_architecture/api_contracts.md
3. docs/03_module_prds/frontend_module.md   ← THIS FILE

YOUR JOB:
Build a single-file HTML frontend (notes-app-ui.html) for the Notes API.

DESIGN SYSTEM: WIRED editorial design language (see this PRD).
The UI must look like a digital magazine, NOT a SaaS app.

KEY CONSTRAINTS:
- MAX 250 lines per logical section — split into <style>, <script> blocks if needed
- Zero border-radius on buttons/inputs (rounded-none = 0px) — NON-NEGOTIABLE
- Zero drop-shadows — hairline borders only for elevation
- No gradients — white canvas + black ink only
- Fonts: Playfair Display (headlines), Lora (body), Manrope (sans/UI)
- Display font weight: 400 only — never bold on headlines
- Color palette: ONLY #000000, #ffffff, #f5f5f5, #e0e0e0, #757575
  (link blue #057dbc for inline note body text ONLY — not buttons/nav)

VIEWS TO BUILD:
1. Auth view (login + register tabs)
2. Dashboard (magazine grid + story rows)
3. Note editor (create / edit)
4. Note detail (article layout)

STATE:
- All state in memory (NO localStorage — sandbox blocked)
- JWT stored in JS variable only
- Hash-based routing (#dashboard, #new, #note/id, #auth)

API:
- Set API_BASE = 'https://your-app.render.com' at top of script
- Use fetch() with Authorization: Bearer <token> header
- Handle all error states with inline error messages (no toasts)

WIRED RULES — VERIFY BEFORE SUBMITTING:
- [ ] Every button has border-radius: 0
- [ ] Every input has border-radius: 0
- [ ] No box-shadow anywhere except none
- [ ] No gradient in any background
- [ ] Playfair Display used for all headings > 20px
- [ ] Story rows separated by 1px solid #e0e0e0
- [ ] Footer is pure black (#000000) background
- [ ] Masthead wordmark is centred, hairline below
- [ ] Pinned notes show "PINNED" badge and appear first
```
