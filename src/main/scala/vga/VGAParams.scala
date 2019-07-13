package vga

import chisel3._

trait VGAParams {
  val horizontalPeriod = 800.U(10.W)
  val horizontalFront = 16.U(10.W)
  val horizontalWidth = 96.U(10.W)
  val horizontalBack = 48.U(10.W)

  val verticalPeriod = 525.U(10.W)
  val verticalFront = 10.U(10.W)
  val verticalWidth = 2.U(10.W)
  val verticalBack = 33.U(10.W)

  val horizontalSignalStart = horizontalFront - 1.U(10.W)
  val horizontalSignalEnd = horizontalFront + horizontalWidth - 1.U(10.W)
  val verticalSignalStart = verticalFront
  val verticalSignalEnd = verticalFront + verticalWidth

  val horizontalBlank = horizontalFront + horizontalWidth + horizontalBack
  val verticalBlank = verticalFront + verticalWidth + verticalBack

  val horizontalSize = 80.U(10.W)
  val verticalSize = 120.U(10.W)
}
