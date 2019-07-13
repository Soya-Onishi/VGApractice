import chisel3._
import vga.Top

object Main extends App {
  Driver.execute(args, () => new Top)
}
