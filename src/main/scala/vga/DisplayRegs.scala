package vga

import chisel3._
import chisel3.util.MuxLookup

class DisplayRegs extends Module {
  val io = IO(new Bundle {
    val slide = Input(Bool())
    val frameCountUp = Input(Bool())
    val slideHorizontal = Input(Bool())
    val slideVertical = Input(Bool())

    val rgb = Output(new Bundle {
      val r = UInt(8.W)
      val g = UInt(8.W)
      val b = UInt(8.W)
    })

    val rgbBool = Output(new Bundle{
      val r = Bool()
      val g = Bool()
      val b = Bool()
    })
  })

  val frameCounter = RegInit(0.U(2.W))
  when(io.frameCountUp) {
    when(frameCounter === 3.U) {
      frameCounter := 0.U
    } .otherwise {
      frameCounter := frameCounter + 1.U
    }
  }

  val doSlide = frameCounter === 3.U && io.frameCountUp


  val initColors = (0 until 4).map {
    v => (0 until 8).map {
      h => ((v + h) % 8).U(3.W)
    }
  }

  val regs = (0 until 60).map {
    v => (0 until 80).map {
      h => RegInit(initColors(v / 15)(h / 10))
    }
  }

  /*
  regs.foreach {
    hRegs =>
      hRegs.foldLeft(hRegs.last){
        case (left, right) =>
          left := Mux(io.slideHorizontal && doSlide, right, left)
          right
      }
  }

  regs.foldLeft(regs.last) {
    case (above, below) =>
      above.zip(below).foreach {
        case (ab, be) =>
          ab := Mux(io.slideVertical && doSlide, be, ab)
      }

      below
  }
  */

  regs.foldLeft(regs.last) {
    case (above, below) =>
      below.tail.foldLeft(below.head) {
        case (left, right) =>
          left := Mux(io.slide, right, left)
          right
      }

      above.last := Mux(io.slide, below(0), above.last)
      below
  }

  val rgb = regs(0)(0)
  val r = rgb(0).asBool()
  val g = rgb(1).asBool()
  val b = rgb(2).asBool()

  io.rgb.r := Mux(r, 255.U, 0.U)
  io.rgb.g := Mux(g, 255.U, 0.U)
  io.rgb.b := Mux(b, 255.U, 0.U)

  io.rgbBool.r := r
  io.rgbBool.g := g
  io.rgbBool.b := b
}
