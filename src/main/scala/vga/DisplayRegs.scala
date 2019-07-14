package vga

import chisel3._
import chisel3.util.MuxLookup

class DisplayRegs extends Module {
  val io = IO(new Bundle {
    val slide = Input(Bool())
    val startLine = Input(Bool())
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

  val horizontalCounter = RegInit(0.U(3.W))
  val verticalCounter = RegInit(0.U(3.W))

  when(io.slide) {
    horizontalCounter := Mux(horizontalCounter === 7.U, 0.U, horizontalCounter + 1.U)
  }
  val shiftLeft = io.slide && (horizontalCounter === 7.U)

  when(io.startLine) {
    verticalCounter := Mux(verticalCounter === 7.U, 0.U, verticalCounter + 1.U)
  }
  val shiftUp = io.startLine && (verticalCounter === 7.U)



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
      below.foldLeft(below.last) {
        case (left, right) =>
          left := Mux(shiftLeft, right, left)
          right
      }

      above.zip(below).foreach {
        case(ab, be) =>
          ab := Mux(shiftUp, be, ab)
      }

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
