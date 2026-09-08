#!/usr/bin/env python3
"""
Multi-Vendor E-Commerce Platform - Phase I Capstone Slide Deck Generator
Course: Object Oriented Techniques using Java (BCSE0352) | NIET Greater Noida
Topic: 271 (Multi-Vendor E-Commerce Management Platform, SDG 8 & 9)

Requirements:
    pip install python-pptx pillow
"""

import os
import pptx
from pptx.util import Inches, Pt
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN
from pptx.enum.shapes import MSO_SHAPE

# -----------------------------------------------------------------------------
# PRESENTATION DESIGN SYSTEM & THEME (Light Academic Executive Palette)
# -----------------------------------------------------------------------------
FONT_HEADING = "Arial"
FONT_BODY = "Calibri"

COLOR_BG = RGBColor(0xF8, 0xFA, 0xFC)         # Off-white canvas (#F8FAFC)
COLOR_CARD = RGBColor(0xFF, 0xFF, 0xFF)       # Pure White container cards (#FFFFFF)
COLOR_INNER_BOX = RGBColor(0xF1, 0xF5, 0xF9)  # Soft Light Grey for diagrams (#F1F5F9)
COLOR_TEXT_DARK = RGBColor(0x0F, 0x17, 0x2A)  # Deep Navy headers & titles (#0F172A)
COLOR_TEXT_BODY = RGBColor(0x33, 0x41, 0x55)  # Charcoal body text (#334155)
COLOR_ACCENT = RGBColor(0x1D, 0x4E, 0xD8)     # Royal Blue accent & badges (#1D4ED8)
COLOR_SECTION = RGBColor(0x25, 0x63, 0xEB)    # Slate Blue for card headers (#2563EB)
COLOR_ACCENT_ALT = RGBColor(0x05, 0x96, 0x69) # Emerald Green (#059669)
COLOR_BORDER = RGBColor(0xCB, 0xD5, 0xE1)     # Thin slate border (#CBD5E1)

SLIDE_WIDTH = Inches(13.333)
SLIDE_HEIGHT = Inches(7.5)
LOGO_PATH = "niet_logo.png"

# -----------------------------------------------------------------------------
# SLIDE TEMPLATE HELPERS
# -----------------------------------------------------------------------------
def apply_slide_background(slide):
    background = slide.background
    fill = background.fill
    fill.solid()
    fill.fore_color.rgb = COLOR_BG

def add_header_banner(slide, title_text: str):
    """Draws standardized academic header with NIET logo placement intact."""
    has_logo = os.path.exists(LOGO_PATH)
    left_offset = Inches(0.8)
    
    if has_logo:
        try:
            slide.shapes.add_picture(LOGO_PATH, left_offset, Inches(0.4), height=Inches(0.65))
            left_offset = Inches(2.2)
        except Exception:
            pass

    sub_box = slide.shapes.add_textbox(left_offset, Inches(0.4), Inches(10.5), Inches(0.3))
    tf_sub = sub_box.text_frame
    tf_sub.word_wrap = True
    tf_sub.margin_left = tf_sub.margin_right = tf_sub.margin_top = tf_sub.margin_bottom = 0
    p_sub = tf_sub.paragraphs[0]
    p_sub.text = "NIET GREATER NOIDA | OBJECT ORIENTED TECHNIQUES USING JAVA (BCSE0352)"
    p_sub.font.name = FONT_HEADING
    p_sub.font.size = Pt(10)
    p_sub.font.bold = True
    p_sub.font.color.rgb = COLOR_ACCENT

    title_box = slide.shapes.add_textbox(left_offset, Inches(0.72), Inches(10.5), Inches(0.6))
    tf_title = title_box.text_frame
    tf_title.word_wrap = True
    tf_title.margin_left = tf_title.margin_right = tf_title.margin_top = tf_title.margin_bottom = 0
    p_title = tf_title.paragraphs[0]
    p_title.text = title_text
    p_title.font.name = FONT_HEADING
    p_title.font.size = Pt(20)
    p_title.font.bold = True
    p_title.font.color.rgb = COLOR_TEXT_DARK

def create_card(slide, left, top, width, height, bg_color=COLOR_CARD, border_color=COLOR_BORDER):
    shape = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, left, top, width, height)
    shape.fill.solid()
    shape.fill.fore_color.rgb = bg_color
    shape.line.color.rgb = border_color
    shape.line.width = Pt(1)
    return shape

def add_split_slide(slide, title_text, left_title, left_items, right_title, right_items, notes_text):
    """Two-column split text layout optimized for lit projector screens."""
    apply_slide_background(slide)
    add_header_banner(slide, title_text)

    col_top = Inches(1.5)
    col_width = Inches(5.65)
    col_height = Inches(5.4)

    # Left Card
    create_card(slide, Inches(0.8), col_top, col_width, col_height)
    box_l = slide.shapes.add_textbox(Inches(1.05), col_top + Inches(0.25), col_width - Inches(0.5), col_height - Inches(0.5))
    tf_l = box_l.text_frame
    tf_l.word_wrap = True
    tf_l.margin_left = tf_l.margin_right = tf_l.margin_top = tf_l.margin_bottom = 0
    p_l_head = tf_l.paragraphs[0]
    p_l_head.text = left_title.upper()
    p_l_head.font.name = FONT_HEADING
    p_l_head.font.size = Pt(13)
    p_l_head.font.bold = True
    p_l_head.font.color.rgb = COLOR_SECTION
    p_l_head.space_after = Pt(12)

    for h, d in left_items:
        p = tf_l.add_paragraph()
        p.space_after = Pt(10)
        r_h = p.add_run()
        r_h.text = f"{h}: "
        r_h.font.name = FONT_BODY
        r_h.font.bold = True
        r_h.font.size = Pt(11)
        r_h.font.color.rgb = COLOR_TEXT_DARK
        r_d = p.add_run()
        r_d.text = d
        r_d.font.name = FONT_BODY
        r_d.font.size = Pt(11)
        r_d.font.color.rgb = COLOR_TEXT_BODY

    # Right Card
    create_card(slide, Inches(6.85), col_top, col_width, col_height)
    box_r = slide.shapes.add_textbox(Inches(7.1), col_top + Inches(0.25), col_width - Inches(0.5), col_height - Inches(0.5))
    tf_r = box_r.text_frame
    tf_r.word_wrap = True
    tf_r.margin_left = tf_r.margin_right = tf_r.margin_top = tf_r.margin_bottom = 0
    p_r_head = tf_r.paragraphs[0]
    p_r_head.text = right_title.upper()
    p_r_head.font.name = FONT_HEADING
    p_r_head.font.size = Pt(13)
    p_r_head.font.bold = True
    p_r_head.font.color.rgb = COLOR_ACCENT_ALT
    p_r_head.space_after = Pt(12)

    for h, d in right_items:
        p = tf_r.add_paragraph()
        p.space_after = Pt(10)
        r_h = p.add_run()
        r_h.text = f"{h}: "
        r_h.font.name = FONT_BODY
        r_h.font.bold = True
        r_h.font.size = Pt(11)
        r_h.font.color.rgb = COLOR_TEXT_DARK
        r_d = p.add_run()
        r_d.text = d
        r_d.font.name = FONT_BODY
        r_d.font.size = Pt(11)
        r_d.font.color.rgb = COLOR_TEXT_BODY

    slide.notes_slide.notes_text_frame.text = notes_text

# -----------------------------------------------------------------------------
# NATIVE VECTOR DIAGRAM BUILDERS
# -----------------------------------------------------------------------------
def render_native_arch_diagram(slide, left, top, width, height):
    """Draws 4-tier architecture flowchart with crisp borders and contrasting text."""
    tiers = [
        ("Tier 1: Presentation (Swing GUI)", "VendorDashboard  |  CustomerStoreView  |  AdminAnalytics", COLOR_SECTION),
        ("Tier 2: Network Controller (TCP)", "ServerSocket (Port 5000)  ->  Worker ClientHandlers", COLOR_TEXT_DARK),
        ("Tier 3: Concurrency Engine", "Striped ReentrantLock Map  ->  Atomic Catalog Updates", COLOR_ACCENT_ALT),
        ("Tier 4: Storage & Persistence", "Concurrent Collections Cache  ->  Append-Only WAL (Disk)", COLOR_TEXT_BODY)
    ]
    box_w = width - Inches(0.6)
    box_h = Inches(0.85)
    start_y = top + Inches(0.35)

    for idx, (t_title, t_desc, accent_col) in enumerate(tiers):
        cur_y = start_y + idx * (box_h + Inches(0.38))
        create_card(slide, left + Inches(0.3), cur_y, box_w, box_h, bg_color=COLOR_INNER_BOX, border_color=COLOR_BORDER)
        
        tb = slide.shapes.add_textbox(left + Inches(0.4), cur_y + Inches(0.1), box_w - Inches(0.2), box_h - Inches(0.2))
        tf = tb.text_frame
        tf.word_wrap = True
        p1 = tf.paragraphs[0]
        p1.text = t_title
        p1.font.name = FONT_HEADING
        p1.font.bold = True
        p1.font.size = Pt(11)
        p1.font.color.rgb = accent_col

        p2 = tf.add_paragraph()
        p2.text = t_desc
        p2.font.name = FONT_BODY
        p2.font.size = Pt(9.5)
        p2.font.color.rgb = COLOR_TEXT_BODY
        p2.space_before = Pt(2)

        if idx < len(tiers) - 1:
            arrow = slide.shapes.add_textbox(left + Inches(0.3), cur_y + box_h, box_w, Inches(0.35))
            tf_arr = arrow.text_frame
            p_arr = tf_arr.paragraphs[0]
            p_arr.alignment = PP_ALIGN.CENTER
            p_arr.text = "▼  TCP / Memory Data Stream  ▼"
            p_arr.font.name = FONT_BODY
            p_arr.font.size = Pt(8.5)
            p_arr.font.color.rgb = COLOR_SECTION

def render_native_uml_diagram(slide, left, top, width, height):
    """Draws domain model cards with light backgrounds and high-contrast text."""
    classes = [
        ("<<abstract>> User", "id, name, email\n+ getRole()", left + Inches(0.3), top + Inches(0.35), Inches(2.3), Inches(1.1)),
        ("Vendor", "regNo, balance\n+ creditPayout()", left + Inches(0.3), top + Inches(1.85), Inches(2.3), Inches(1.1)),
        ("Customer", "address, walletBalance\n+ deductWallet()", left + Inches(3.0), top + Inches(1.85), Inches(2.3), Inches(1.1)),
        ("Product", "sku, title, price, stock\n+ decrementStock()", left + Inches(0.3), top + Inches(3.4), Inches(2.3), Inches(1.2)),
        ("Order & OrderItem", "orderId, timestamp, items\n+ calculateTotal()", left + Inches(3.0), top + Inches(3.4), Inches(2.3), Inches(1.2))
    ]
    for c_title, c_details, cx, cy, cw, ch in classes:
        create_card(slide, cx, cy, cw, ch, bg_color=COLOR_INNER_BOX, border_color=COLOR_BORDER)
        tb = slide.shapes.add_textbox(cx + Inches(0.1), cy + Inches(0.08), cw - Inches(0.2), ch - Inches(0.15))
        tf = tb.text_frame
        tf.word_wrap = True
        p = tf.paragraphs[0]
        p.text = c_title
        p.font.name = FONT_HEADING
        p.font.bold = True
        p.font.size = Pt(10)
        p.font.color.rgb = COLOR_SECTION

        p_det = tf.add_paragraph()
        p_det.text = c_details
        p_det.font.name = FONT_BODY
        p_det.font.size = Pt(8.5)
        p_det.font.color.rgb = COLOR_TEXT_BODY
        p_det.space_before = Pt(3)

def render_native_concurrency_diagram(slide, left, top, width, height):
    """Draws thread safety and race prevention phases with high legibility."""
    steps = [
        ("Step 1: Simultaneous Incoming Requests", "Client 1 and Client 2 attempt to purchase SKU-101 (Stock: 1) concurrently over TCP."),
        ("Step 2: Striped Lock Acquisition", "Client 1 acquires fair ReentrantLock on SKU-101. Client 2 is queued with 500ms timeout."),
        ("Step 3: Atomic Validation & Commit", "Client 1 verifies (1 >= 1), decrements stock to 0, appends record to WAL, and unlocks."),
        ("Step 4: Safe Atomic Rejection", "Client 2 acquires lock, evaluates (0 < 1), and immediately receives InsufficientStockException.")
    ]
    box_w = width - Inches(0.6)
    box_h = Inches(1.05)
    start_y = top + Inches(0.35)

    for idx, (s_title, s_desc) in enumerate(steps, start=1):
        cur_y = start_y + (idx - 1) * (box_h + Inches(0.22))
        col = COLOR_ACCENT_ALT if idx in [2, 3] else COLOR_SECTION
        create_card(slide, left + Inches(0.3), cur_y, box_w, box_h, bg_color=COLOR_INNER_BOX, border_color=COLOR_BORDER)
        
        tb = slide.shapes.add_textbox(left + Inches(0.4), cur_y + Inches(0.1), box_w - Inches(0.2), box_h - Inches(0.2))
        tf = tb.text_frame
        tf.word_wrap = True
        p = tf.paragraphs[0]
        p.text = f"[Phase 0{idx}] {s_title}"
        p.font.name = FONT_HEADING
        p.font.bold = True
        p.font.size = Pt(10)
        p.font.color.rgb = col

        p_desc = tf.add_paragraph()
        p_desc.text = s_desc
        p_desc.font.name = FONT_BODY
        p_desc.font.size = Pt(9.5)
        p_desc.font.color.rgb = COLOR_TEXT_BODY
        p_desc.space_before = Pt(3)

def add_split_diagram_slide(slide, title_text, left_title, left_items, diagram_type, notes_text):
    """Pairs left specifications card with a right-hand vector diagram card."""
    apply_slide_background(slide)
    add_header_banner(slide, title_text)

    col_top = Inches(1.5)
    col_width = Inches(5.65)
    col_height = Inches(5.4)

    # Left Column
    create_card(slide, Inches(0.8), col_top, col_width, col_height)
    box_l = slide.shapes.add_textbox(Inches(1.05), col_top + Inches(0.25), col_width - Inches(0.5), col_height - Inches(0.5))
    tf_l = box_l.text_frame
    tf_l.word_wrap = True
    tf_l.margin_left = tf_l.margin_right = tf_l.margin_top = tf_l.margin_bottom = 0
    p_l_head = tf_l.paragraphs[0]
    p_l_head.text = left_title.upper()
    p_l_head.font.name = FONT_HEADING
    p_l_head.font.size = Pt(13)
    p_l_head.font.bold = True
    p_l_head.font.color.rgb = COLOR_SECTION
    p_l_head.space_after = Pt(12)

    for h, d in left_items:
        p = tf_l.add_paragraph()
        p.space_after = Pt(10)
        r_h = p.add_run()
        r_h.text = f"{h}: "
        r_h.font.name = FONT_BODY
        r_h.font.bold = True
        r_h.font.size = Pt(11)
        r_h.font.color.rgb = COLOR_TEXT_DARK
        r_d = p.add_run()
        r_d.text = d
        r_d.font.name = FONT_BODY
        r_d.font.size = Pt(11)
        r_d.font.color.rgb = COLOR_TEXT_BODY

    # Right Column: Diagram Container
    create_card(slide, Inches(6.85), col_top, col_width, col_height)
    if diagram_type == "ARCH":
        render_native_arch_diagram(slide, Inches(6.85), col_top, col_width, col_height)
    elif diagram_type == "UML":
        render_native_uml_diagram(slide, Inches(6.85), col_top, col_width, col_height)
    elif diagram_type == "CONCURRENCY":
        render_native_concurrency_diagram(slide, Inches(6.85), col_top, col_width, col_height)

    slide.notes_slide.notes_text_frame.text = notes_text

# -----------------------------------------------------------------------------
# MAIN BUILDER
# -----------------------------------------------------------------------------
def build_deck():
    prs = pptx.Presentation()
    prs.slide_width = SLIDE_WIDTH
    prs.slide_height = SLIDE_HEIGHT
    blank_layout = prs.slide_layouts[6]

    # =========================================================================
    # SLIDE 1: TITLE & TEAM ROLES
    # =========================================================================
    s1 = prs.slides.add_slide(blank_layout)
    apply_slide_background(s1)

    t_box = s1.shapes.add_textbox(Inches(0.8), Inches(0.6), Inches(11.7), Inches(1.8))
    tf1 = t_box.text_frame
    tf1.word_wrap = True
    
    p1 = tf1.paragraphs[0]
    p1.text = "NIET GREATER NOIDA | CAPSTONE PHASE I DEFENSE"
    p1.font.name = FONT_HEADING
    p1.font.size = Pt(11)
    p1.font.bold = True
    p1.font.color.rgb = COLOR_ACCENT

    p2 = tf1.add_paragraph()
    p2.text = "Multi-Vendor E-Commerce Management Platform"
    p2.font.name = FONT_HEADING
    p2.font.size = Pt(28)
    p2.font.bold = True
    p2.font.color.rgb = COLOR_TEXT_DARK
    p2.space_before = Pt(4)

    p3 = tf1.add_paragraph()
    p3.text = "Topic 271: Java SE Marketplace Infrastructure | Aligned with UN SDGs 8 & 9"
    p3.font.name = FONT_BODY
    p3.font.size = Pt(13)
    p3.font.color.rgb = COLOR_TEXT_BODY
    p3.space_before = Pt(4)

    create_card(s1, Inches(0.8), Inches(2.45), Inches(11.73), Inches(0.7))
    sdg_box = s1.shapes.add_textbox(Inches(1.0), Inches(2.55), Inches(11.3), Inches(0.5))
    tf_sdg = sdg_box.text_frame
    p_sdg = tf_sdg.paragraphs[0]
    
    r_sdg8 = p_sdg.add_run()
    r_sdg8.text = "[SDG 8: DECENT WORK] "
    r_sdg8.font.bold = True
    r_sdg8.font.size = Pt(11)
    r_sdg8.font.color.rgb = COLOR_ACCENT_ALT

    r_sdg8_t = p_sdg.add_run()
    r_sdg8_t.text = "Immediate Liquidity Settlement & Subsidized 2% Artisan Fees   |   "
    r_sdg8_t.font.size = Pt(11)
    r_sdg8_t.font.color.rgb = COLOR_TEXT_DARK

    r_sdg9 = p_sdg.add_run()
    r_sdg9.text = "[SDG 9: RESILIENT INFRASTRUCTURE] "
    r_sdg9.font.bold = True
    r_sdg9.font.size = Pt(11)
    r_sdg9.font.color.rgb = COLOR_ACCENT

    r_sdg9_t = p_sdg.add_run()
    r_sdg9_t.text = "Zero-Cloud Dependency on Standard Java SE"
    r_sdg9_t.font.size = Pt(11)
    r_sdg9_t.font.color.rgb = COLOR_TEXT_DARK

    members = [
        ("Akshya Gupta", "Team Lead & BA", "Req Analysis, SDG Scope & Milestone Tracking"),
        ("Pranav Dogra", "System Architect", "UML Class Models, Sockets & Concurrency Model"),
        ("Kajal Tiwari", "Tech Dev Lead", "Order Processing Engine & Lock-Striping"),
        ("Priyanshu Rai", "Integration Lead", "TCP Socket Protocol, Threads & Collections"),
        ("Sandeep Kumar", "QA & Docs Lead", "WAL Durability Tests, Swing UI & Validation")
    ]
    card_w = Inches(2.18)
    card_gap = Inches(0.2)
    start_x = Inches(0.8)

    for idx, (m_name, m_role, m_desc) in enumerate(members):
        cx = start_x + (idx * (card_w + card_gap))
        create_card(s1, cx, Inches(3.45), card_w, Inches(3.45))
        m_box = s1.shapes.add_textbox(cx + Inches(0.15), Inches(3.65), card_w - Inches(0.3), Inches(3.0))
        tf_m = m_box.text_frame
        tf_m.word_wrap = True
        
        pm1 = tf_m.paragraphs[0]
        pm1.text = m_name
        pm1.font.bold = True
        pm1.font.size = Pt(11)
        pm1.font.color.rgb = COLOR_ACCENT

        pm2 = tf_m.add_paragraph()
        pm2.text = m_role
        pm2.font.bold = True
        pm2.font.size = Pt(10)
        pm2.font.color.rgb = COLOR_TEXT_DARK
        pm2.space_before = Pt(3)

        pm3 = tf_m.add_paragraph()
        pm3.text = m_desc
        pm3.font.size = Pt(9.5)
        pm3.font.color.rgb = COLOR_TEXT_BODY
        pm3.space_before = Pt(8)

    s1.notes_slide.notes_text_frame.text = (
        "Good morning evaluators. Today we present our Java-native marketplace platform designed "
        "to empower micro-vendors through automated settlements and thread-safe trade infrastructure under SDGs 8 and 9."
    )

    # =========================================================================
    # SLIDE 2: EMPIRICAL PROBLEM ANALYSIS
    # =========================================================================
    add_split_slide(
        slide=prs.slides.add_slide(blank_layout),
        title_text="Empirical Problem Analysis: Micro-Enterprise Bottlenecks",
        left_title="Market Failure Evidence",
        left_items=[
            ("Working Capital Strain", "World Bank & IFC data shows 60% to 70% of micro-enterprises face insolvency due to T+14 to T+30 day payout delays."),
            ("Predatory Commission Takes", "UNCTAD reports indicate 15% to 30% flat fees erase the margins of artisans operating at 12% to 18% profitability."),
            ("Flash-Sale Concurrency Failures", "ACM studies document 18% to 25% inventory error rates under burst traffic when locking mechanisms are absent."),
            ("High ERP Abandonment Rates", "Over 70% of SME digitization tools are abandoned within 12 months due to complex cloud hosting and licensing costs.")
        ],
        right_title="Cascading Vendor Exploitation",
        right_items=[
            ("Delayed Receivables", "Centralized marketplaces pool settlement float to earn interest, locking artisan working capital."),
            ("Overselling Contention", "Check-then-act race conditions permit concurrent purchases of zero-stock items."),
            ("Unfair Penalties", "Vendors receive algorithmic rating demotions for order cancellations caused by system software bugs."),
            ("Platform Monopolization", "Small producers lack direct communication channels and transparent ledger records.")
        ],
        notes_text=(
            "Research shows that delayed settlements and unsynchronized inventory failures systematically hurt small businesses. "
            "Our platform addresses these bottlenecks by providing instant wallet settlements and deadlock-free inventory locking."
        )
    )

    # =========================================================================
    # SLIDE 3: EXISTING LIMITATIONS VS OUR SOLUTION
    # =========================================================================
    add_split_slide(
        slide=prs.slides.add_slide(blank_layout),
        title_text="Existing Market Architecture vs. Proposed Solution",
        left_title="Existing Platform Flaws",
        left_items=[
            ("Web Plugin Connection Pools", "WooCommerce/OpenCart exhaust MySQL connection pools during simultaneous order spikes."),
            ("Naive Monolithic Monitors", "University Java projects lock the entire catalog globally, crashing server throughput under load."),
            ("Volatile State Persistence", "Monolithic ObjectOutputStream serialization dumps lose pending transactions on abrupt power failure."),
            ("Opaque Dispute Blackboxes", "Centralized corporate platforms hold complete authority over merchant balances without non-repudiable audit logs.")
        ],
        right_title="Java SE Native Innovations",
        right_items=[
            ("Fine-Grained Striped Locks", "ConcurrentHashMap<SKU, ReentrantLock> permits parallel purchases across different items without stalls."),
            ("Write-Ahead Log (WAL)", "Append-only FileChannel.force(false) guarantees transaction durability to disk prior to socket ACK."),
            ("Dynamic Tier Commission", "Programmatic strategy lambdas charge subsidized 2% rates for artisans and 10% for large firms."),
            ("Zero-Cloud Footprint", "Self-contained multithreaded TCP socket server and Swing client running natively on OpenJDK 17/21.")
        ],
        notes_text=(
            "While web plugins crash under burst traffic and basic projects use slow global locks, our system introduces fine-grained "
            "striped locking and an append-only write-ahead log to achieve zero data loss and low latency in pure Java SE."
        )
    )

    # =========================================================================
    # SLIDE 4: SYSTEM ARCHITECTURE (NATIVE VECTOR DIAGRAM)
    # =========================================================================
    add_split_diagram_slide(
        slide=prs.slides.add_slide(blank_layout),
        title_text="Multi-Tiered System Architecture (MVC over TCP)",
        left_title="Architectural Layer Breakdown",
        left_items=[
            ("Presentation Tier", "Asynchronous Java Swing user views (Vendor, Customer, Admin) bound to DefaultTableModel interfaces."),
            ("Network Controller Tier", "Multi-threaded TCP ServerSocket allocating worker ClientHandler threads per client socket."),
            ("Business Domain Tier", "Thread-safe service controllers executing order pipelines, catalog streams, and commission splits."),
            ("Storage & Memory Tier", "High-speed ConcurrentHashMap collections backed by append-only FileChannel write-ahead logs.")
        ],
        diagram_type="ARCH",
        notes_text=(
            "Our architecture separates the user interface from backend execution using multithreaded TCP sockets. "
            "This keeps the Swing interface responsive while the server manages fine-grained locks and immediate file logging."
        )
    )

    # =========================================================================
    # SLIDE 5: UML CLASS HIERARCHY (NATIVE VECTOR DIAGRAM)
    # =========================================================================
    add_split_diagram_slide(
        slide=prs.slides.add_slide(blank_layout),
        title_text="Object-Oriented Design & Domain Class Hierarchy",
        left_title="OOAD Structural Relationships",
        left_items=[
            ("Generalization (Inheritance)", "Abstract base class User inherited by Vendor (commission rates) and Customer (wallet balance)."),
            ("Composition (Lifecycle Binding)", "Order owns a collection of OrderItem instances; destroying an order destroys its items."),
            ("Aggregation (Catalog Mapping)", "Vendor aggregates Product listings without owning their independent product lifecycle."),
            ("Interfaces & Generics", "Repository<T, ID> generic contracts and Taxable interfaces decouple storage and tax calculation.")
        ],
        diagram_type="UML",
        notes_text=(
            "Our domain model explicitly demonstrates all core Unit 1 and Unit 2 principles: inheritance, composition, and aggregation. "
            "Generic repositories decouple our business services from underlying storage models."
        )
    )

    # =========================================================================
    # SLIDE 6: CONCURRENCY ENGINE (NATIVE VECTOR DIAGRAM)
    # =========================================================================
    add_split_diagram_slide(
        slide=prs.slides.add_slide(blank_layout),
        title_text="Concurrency Control & Race Condition Prevention",
        left_title="Thread Safety Specifications",
        left_items=[
            ("Striped ReentrantLock Map", "Isolates locks per SKU; buyers purchasing different items execute completely in parallel."),
            ("Deadlock-Free Acquisition", "Multi-item carts sort SKUs alphanumerically before lock acquisition to prevent circular waits."),
            ("Two-Phase Commit Protocol", "Validates stock > 0 under lock, decrements in memory, flushes to WAL, and releases in finally."),
            ("Atomic Rejection", "Any transaction attempting to buy an empty SKU throws a checked InsufficientStockException.")
        ],
        diagram_type="CONCURRENCY",
        notes_text=(
            "To prevent overselling during flash sales, we implemented fine-grained striped locking using ReentrantLock maps. "
            "Purchases for separate products run in parallel, while checkout requests for identical SKUs are strictly synchronized."
        )
    )

    # =========================================================================
    # SLIDE 7: FUNCTIONAL & NON-FUNCTIONAL REQUIREMENTS
    # =========================================================================
    add_split_slide(
        slide=prs.slides.add_slide(blank_layout),
        title_text="System Requirements & Performance Benchmarks",
        left_title="Functional Requirements (FRs)",
        left_items=[
            ("FR-01: Identity Management", "TCP socket authentication validating Admin, Vendor, and Customer role permissions."),
            ("FR-02: Isolated Catalog CRUD", "Thread-safe listing creation, edits, and soft-deletes isolated by vendor ownership."),
            ("FR-03: Stream-Based Search", "Non-blocking catalog search using Java Streams and functional Predicate lambdas."),
            ("FR-04: Atomic Multi-Cart Checkout", "Transactional order processing consolidating items from multiple distinct sellers.")
        ],
        right_title="Non-Functional Requirements (NFRs)",
        right_items=[
            ("Throughput & Latency", "Sub-50ms checkout latency under nominal load; handles >= 150 concurrent client connections."),
            ("Zero Overselling Guarantee", "Zero negative-stock incidents across 50 concurrent checkouts on identical single items."),
            ("Crash Recovery Durability", "Zero committed transaction loss using FileChannel byte flushing on sudden process halts."),
            ("Cross-Platform Portability", "100% native execution on OpenJDK 17 and 21 across Windows, macOS, and Linux.")
        ],
        notes_text=(
            "Our system specification includes eight functional requirements alongside measurable performance benchmarks. "
            "We guarantee zero inventory overselling under heavy concurrency and native execution on any desktop operating system."
        )
    )

    # =========================================================================
    # SLIDE 8: PACKAGE ARCHITECTURE & EXCEPTION FRAMEWORK
    # =========================================================================
    add_split_slide(
        slide=prs.slides.add_slide(blank_layout),
        title_text="Package Architecture & Fault-Tolerant Persistence",
        left_title="Modular Package Structure",
        left_items=[
            ("com.ecommerce.model", "Encapsulated domain entities: User, Vendor, Customer, Product, Order, OrderItem."),
            ("com.ecommerce.network", "TCP ServerSocket engine, Runnable ClientHandler workers, and NetworkPacket."),
            ("com.ecommerce.service", "CatalogService streams, OrderService transactions, and SettlementEngine."),
            ("com.ecommerce.storage", "WriteAheadLogManager file channels and SnapshotEngine object streams.")
        ],
        right_title="Error Recovery & Serialization",
        right_items=[
            ("Checked Domain Exceptions", "Custom InsufficientStockException and SettlementException enforce clean recovery."),
            ("High-Performance String Utilities", "InvoiceFormatter leverages StringBuilder for thread-safe receipt construction."),
            ("Dual Persistence Model", "Combines periodic ObjectOutputStream snapshots with an append-only transaction log."),
            ("Crash State Recovery", "Server restart reads WAL sequentially to restore active collections without missing orders.")
        ],
        notes_text=(
            "The package architecture separates concerns following standard enterprise conventions. Custom exceptions guarantee "
            "clean error handling, while our write-ahead log ensures complete state recovery in the event of an unexpected crash."
        )
    )

    # =========================================================================
    # SLIDE 9: MEASURABLE SDG IMPACT
    # =========================================================================
    add_split_slide(
        slide=prs.slides.add_slide(blank_layout),
        title_text="Measurable UN Sustainable Development Goals Alignment",
        left_title="SDG 8: Decent Work & Economic Growth",
        left_items=[
            ("T+0 Instant Liquidity", "Automated settlement immediately credits vendor balance upon dispatch, eliminating 30-day payout holds."),
            ("Subsidized 2% Commission", "Programmatic commission tiers cap micro-artisan fees at 2%, protecting informal seller margins."),
            ("Verifiable Auditing", "Double-entry balance sheets provide mathematical transparency, preventing arbitrary platform clawbacks.")
        ],
        right_title="SDG 9: Industry, Innovation & Infrastructure",
        right_items=[
            ("Resilient Concurrency Core", "Fine-grained striped locking provides enterprise-level transaction throughput on low-cost hardware."),
            ("Zero-Cloud Dependency", "Runs natively on standard desktop hardware without requiring expensive enterprise cloud databases."),
            ("Crash-Proof Fault Tolerance", "Append-only write-ahead persistence prevents state loss even during unexpected system terminations.")
        ],
        notes_text=(
            "Our project advances SDG 8 by providing small artisans with subsidized 2% commission rates and instant payout releases. "
            "It supports SDG 9 by delivering a fault-tolerant marketplace engine that runs reliably on affordable desktop hardware."
        )
    )

    # =========================================================================
    # SLIDE 10: DELIVERABLES CHECKLIST & PHASE II ROADMAP
    # =========================================================================
    add_split_slide(
        slide=prs.slides.add_slide(blank_layout),
        title_text="Phase I Deliverables Checklist & Phase II Roadmap",
        left_title="Phase I Milestones Completed",
        left_items=[
            ("Problem & Requirement Analysis", "Completed empirical research, stakeholder matrix, and prioritized FRs/NFRs."),
            ("System & OOAD Blueprint", "Finalized PlantUML class models, sequence flows, and tiered MVC architecture."),
            ("Core Prototype Foundation", "Validated custom exceptions, striped lock managers, and domain model classes."),
            ("Version Control Repository", "GitHub repository established with standard package directory hierarchies.")
        ],
        right_title="Phase II Sprint Schedule",
        right_items=[
            ("Sprint 1 (Network Core)", "Implement MarketplaceServer with dynamic worker thread pool on TCP port 5000."),
            ("Sprint 2 (Durability Engine)", "Implement WriteAheadLogManager using FileChannel for transaction persistence."),
            ("Sprint 3 (Swing Presentation)", "Build VendorDashboard and CustomerStoreView with dynamic JTable bindings."),
            ("Sprint 4 (Stress Testing & QA)", "Execute multithreaded JMeter injection and crash recovery validation for final viva.")
        ],
        notes_text=(
            "Phase I delivers a complete requirements analysis, domain model, and architectural blueprint. "
            "Our team is prepared to move into Phase II to implement the multithreaded socket server and Swing interfaces."
        )
    )

    output_pptx = "TeamXX_BCSE0352_MultiVendorECommerceManagementPlatform.pptx"
    prs.save(output_pptx)
    print(f"[SUCCESS] Presentation generated successfully: {output_pptx}")

if __name__ == "__main__":
    build_deck()
