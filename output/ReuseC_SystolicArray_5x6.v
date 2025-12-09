// Generator : SpinalHDL v1.12.3    git head : 591e64062329e5e2e2b81f4d52422948053edb97
// Component : ReuseC_SystolicArray_5x6
// Git hash  : 39943a2e0683cd44058f7944b25587b508e528a7

`timescale 1ns/1ps

module ReuseC_SystolicArray_5x6 (
  input  wire [7:0]    io_inputA_0,
  input  wire [7:0]    io_inputA_1,
  input  wire [7:0]    io_inputA_2,
  input  wire [7:0]    io_inputA_3,
  input  wire [7:0]    io_inputA_4,
  input  wire [7:0]    io_inputB_0,
  input  wire [7:0]    io_inputB_1,
  input  wire [7:0]    io_inputB_2,
  input  wire [7:0]    io_inputB_3,
  input  wire [7:0]    io_inputB_4,
  input  wire [7:0]    io_inputB_5,
  input  wire          io_outputCaptureEnableC_0_0,
  input  wire          io_outputCaptureEnableC_0_1,
  input  wire          io_outputCaptureEnableC_0_2,
  input  wire          io_outputCaptureEnableC_0_3,
  input  wire          io_outputCaptureEnableC_0_4,
  input  wire          io_outputCaptureEnableC_0_5,
  input  wire          io_outputCaptureEnableC_1_0,
  input  wire          io_outputCaptureEnableC_1_1,
  input  wire          io_outputCaptureEnableC_1_2,
  input  wire          io_outputCaptureEnableC_1_3,
  input  wire          io_outputCaptureEnableC_1_4,
  input  wire          io_outputCaptureEnableC_1_5,
  input  wire          io_outputCaptureEnableC_2_0,
  input  wire          io_outputCaptureEnableC_2_1,
  input  wire          io_outputCaptureEnableC_2_2,
  input  wire          io_outputCaptureEnableC_2_3,
  input  wire          io_outputCaptureEnableC_2_4,
  input  wire          io_outputCaptureEnableC_2_5,
  input  wire          io_outputCaptureEnableC_3_0,
  input  wire          io_outputCaptureEnableC_3_1,
  input  wire          io_outputCaptureEnableC_3_2,
  input  wire          io_outputCaptureEnableC_3_3,
  input  wire          io_outputCaptureEnableC_3_4,
  input  wire          io_outputCaptureEnableC_3_5,
  input  wire          io_outputCaptureEnableC_4_0,
  input  wire          io_outputCaptureEnableC_4_1,
  input  wire          io_outputCaptureEnableC_4_2,
  input  wire          io_outputCaptureEnableC_4_3,
  input  wire          io_outputCaptureEnableC_4_4,
  input  wire          io_outputCaptureEnableC_4_5,
  input  wire          io_resetPartialC_0_0,
  input  wire          io_resetPartialC_0_1,
  input  wire          io_resetPartialC_0_2,
  input  wire          io_resetPartialC_0_3,
  input  wire          io_resetPartialC_0_4,
  input  wire          io_resetPartialC_0_5,
  input  wire          io_resetPartialC_1_0,
  input  wire          io_resetPartialC_1_1,
  input  wire          io_resetPartialC_1_2,
  input  wire          io_resetPartialC_1_3,
  input  wire          io_resetPartialC_1_4,
  input  wire          io_resetPartialC_1_5,
  input  wire          io_resetPartialC_2_0,
  input  wire          io_resetPartialC_2_1,
  input  wire          io_resetPartialC_2_2,
  input  wire          io_resetPartialC_2_3,
  input  wire          io_resetPartialC_2_4,
  input  wire          io_resetPartialC_2_5,
  input  wire          io_resetPartialC_3_0,
  input  wire          io_resetPartialC_3_1,
  input  wire          io_resetPartialC_3_2,
  input  wire          io_resetPartialC_3_3,
  input  wire          io_resetPartialC_3_4,
  input  wire          io_resetPartialC_3_5,
  input  wire          io_resetPartialC_4_0,
  input  wire          io_resetPartialC_4_1,
  input  wire          io_resetPartialC_4_2,
  input  wire          io_resetPartialC_4_3,
  input  wire          io_resetPartialC_4_4,
  input  wire          io_resetPartialC_4_5,
  output wire [31:0]   io_outputC_0,
  output wire [31:0]   io_outputC_1,
  output wire [31:0]   io_outputC_2,
  output wire [31:0]   io_outputC_3,
  output wire [31:0]   io_outputC_4,
  output wire [31:0]   io_outputC_5,
  output wire [31:0]   io_outputC_6,
  output wire [31:0]   io_outputC_7,
  output wire [31:0]   io_outputC_8,
  output wire [31:0]   io_outputC_9,
  input  wire          clk,
  input  wire          reset
);

  wire       [7:0]    pes_0_0_io_outputA;
  wire       [7:0]    pes_0_0_io_outputB;
  wire       [31:0]   pes_0_0_io_outputC;
  wire       [7:0]    pes_0_1_io_outputA;
  wire       [7:0]    pes_0_1_io_outputB;
  wire       [31:0]   pes_0_1_io_outputC;
  wire       [7:0]    pes_0_2_io_outputA;
  wire       [7:0]    pes_0_2_io_outputB;
  wire       [31:0]   pes_0_2_io_outputC;
  wire       [7:0]    pes_0_3_io_outputA;
  wire       [7:0]    pes_0_3_io_outputB;
  wire       [31:0]   pes_0_3_io_outputC;
  wire       [7:0]    pes_0_4_io_outputA;
  wire       [7:0]    pes_0_4_io_outputB;
  wire       [31:0]   pes_0_4_io_outputC;
  wire       [7:0]    pes_0_5_io_outputB;
  wire       [31:0]   pes_0_5_io_outputC;
  wire       [7:0]    pes_1_0_io_outputA;
  wire       [7:0]    pes_1_0_io_outputB;
  wire       [31:0]   pes_1_0_io_outputC;
  wire       [7:0]    pes_1_1_io_outputA;
  wire       [7:0]    pes_1_1_io_outputB;
  wire       [31:0]   pes_1_1_io_outputC;
  wire       [7:0]    pes_1_2_io_outputA;
  wire       [7:0]    pes_1_2_io_outputB;
  wire       [31:0]   pes_1_2_io_outputC;
  wire       [7:0]    pes_1_3_io_outputA;
  wire       [7:0]    pes_1_3_io_outputB;
  wire       [31:0]   pes_1_3_io_outputC;
  wire       [7:0]    pes_1_4_io_outputA;
  wire       [7:0]    pes_1_4_io_outputB;
  wire       [31:0]   pes_1_4_io_outputC;
  wire       [7:0]    pes_1_5_io_outputB;
  wire       [31:0]   pes_1_5_io_outputC;
  wire       [7:0]    pes_2_0_io_outputA;
  wire       [7:0]    pes_2_0_io_outputB;
  wire       [31:0]   pes_2_0_io_outputC;
  wire       [7:0]    pes_2_1_io_outputA;
  wire       [7:0]    pes_2_1_io_outputB;
  wire       [31:0]   pes_2_1_io_outputC;
  wire       [7:0]    pes_2_2_io_outputA;
  wire       [7:0]    pes_2_2_io_outputB;
  wire       [31:0]   pes_2_2_io_outputC;
  wire       [7:0]    pes_2_3_io_outputA;
  wire       [7:0]    pes_2_3_io_outputB;
  wire       [31:0]   pes_2_3_io_outputC;
  wire       [7:0]    pes_2_4_io_outputA;
  wire       [7:0]    pes_2_4_io_outputB;
  wire       [31:0]   pes_2_4_io_outputC;
  wire       [7:0]    pes_2_5_io_outputB;
  wire       [31:0]   pes_2_5_io_outputC;
  wire       [7:0]    pes_3_0_io_outputA;
  wire       [7:0]    pes_3_0_io_outputB;
  wire       [31:0]   pes_3_0_io_outputC;
  wire       [7:0]    pes_3_1_io_outputA;
  wire       [7:0]    pes_3_1_io_outputB;
  wire       [31:0]   pes_3_1_io_outputC;
  wire       [7:0]    pes_3_2_io_outputA;
  wire       [7:0]    pes_3_2_io_outputB;
  wire       [31:0]   pes_3_2_io_outputC;
  wire       [7:0]    pes_3_3_io_outputA;
  wire       [7:0]    pes_3_3_io_outputB;
  wire       [31:0]   pes_3_3_io_outputC;
  wire       [7:0]    pes_3_4_io_outputA;
  wire       [7:0]    pes_3_4_io_outputB;
  wire       [31:0]   pes_3_4_io_outputC;
  wire       [7:0]    pes_3_5_io_outputB;
  wire       [31:0]   pes_3_5_io_outputC;
  wire       [7:0]    pes_4_0_io_outputA;
  wire       [31:0]   pes_4_0_io_outputC;
  wire       [7:0]    pes_4_1_io_outputA;
  wire       [31:0]   pes_4_1_io_outputC;
  wire       [7:0]    pes_4_2_io_outputA;
  wire       [31:0]   pes_4_2_io_outputC;
  wire       [7:0]    pes_4_3_io_outputA;
  wire       [31:0]   pes_4_3_io_outputC;
  wire       [7:0]    pes_4_4_io_outputA;
  wire       [31:0]   pes_4_4_io_outputC;
  wire       [31:0]   pes_4_5_io_outputC;
  wire       [7:0]    skewBuffer_2_io_output_0;
  wire       [7:0]    skewBuffer_2_io_output_1;
  wire       [7:0]    skewBuffer_2_io_output_2;
  wire       [7:0]    skewBuffer_2_io_output_3;
  wire       [7:0]    skewBuffer_2_io_output_4;
  wire       [7:0]    skewBuffer_3_io_output_0;
  wire       [7:0]    skewBuffer_3_io_output_1;
  wire       [7:0]    skewBuffer_3_io_output_2;
  wire       [7:0]    skewBuffer_3_io_output_3;
  wire       [7:0]    skewBuffer_3_io_output_4;
  wire       [7:0]    skewBuffer_3_io_output_5;

  ProcessingElement pes_0_0 (
    .io_inputA               (skewBuffer_2_io_output_0[7:0]), //i
    .io_inputB               (skewBuffer_3_io_output_0[7:0]), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_0_0  ), //i
    .io_resetPartialC        (io_resetPartialC_0_0         ), //i
    .io_outputA              (pes_0_0_io_outputA[7:0]      ), //o
    .io_outputB              (pes_0_0_io_outputB[7:0]      ), //o
    .io_outputC              (pes_0_0_io_outputC[31:0]     ), //o
    .clk                     (clk                          ), //i
    .reset                   (reset                        )  //i
  );
  ProcessingElement pes_0_1 (
    .io_inputA               (pes_0_0_io_outputA[7:0]      ), //i
    .io_inputB               (skewBuffer_3_io_output_1[7:0]), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_0_1  ), //i
    .io_resetPartialC        (io_resetPartialC_0_1         ), //i
    .io_outputA              (pes_0_1_io_outputA[7:0]      ), //o
    .io_outputB              (pes_0_1_io_outputB[7:0]      ), //o
    .io_outputC              (pes_0_1_io_outputC[31:0]     ), //o
    .clk                     (clk                          ), //i
    .reset                   (reset                        )  //i
  );
  ProcessingElement pes_0_2 (
    .io_inputA               (pes_0_1_io_outputA[7:0]      ), //i
    .io_inputB               (skewBuffer_3_io_output_2[7:0]), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_0_2  ), //i
    .io_resetPartialC        (io_resetPartialC_0_2         ), //i
    .io_outputA              (pes_0_2_io_outputA[7:0]      ), //o
    .io_outputB              (pes_0_2_io_outputB[7:0]      ), //o
    .io_outputC              (pes_0_2_io_outputC[31:0]     ), //o
    .clk                     (clk                          ), //i
    .reset                   (reset                        )  //i
  );
  ProcessingElement pes_0_3 (
    .io_inputA               (pes_0_2_io_outputA[7:0]      ), //i
    .io_inputB               (skewBuffer_3_io_output_3[7:0]), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_0_3  ), //i
    .io_resetPartialC        (io_resetPartialC_0_3         ), //i
    .io_outputA              (pes_0_3_io_outputA[7:0]      ), //o
    .io_outputB              (pes_0_3_io_outputB[7:0]      ), //o
    .io_outputC              (pes_0_3_io_outputC[31:0]     ), //o
    .clk                     (clk                          ), //i
    .reset                   (reset                        )  //i
  );
  ProcessingElement pes_0_4 (
    .io_inputA               (pes_0_3_io_outputA[7:0]      ), //i
    .io_inputB               (skewBuffer_3_io_output_4[7:0]), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_0_4  ), //i
    .io_resetPartialC        (io_resetPartialC_0_4         ), //i
    .io_outputA              (pes_0_4_io_outputA[7:0]      ), //o
    .io_outputB              (pes_0_4_io_outputB[7:0]      ), //o
    .io_outputC              (pes_0_4_io_outputC[31:0]     ), //o
    .clk                     (clk                          ), //i
    .reset                   (reset                        )  //i
  );
  ProcessingElement_5 pes_0_5 (
    .io_inputA               (pes_0_4_io_outputA[7:0]      ), //i
    .io_inputB               (skewBuffer_3_io_output_5[7:0]), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_0_5  ), //i
    .io_resetPartialC        (io_resetPartialC_0_5         ), //i
    .io_outputB              (pes_0_5_io_outputB[7:0]      ), //o
    .io_outputC              (pes_0_5_io_outputC[31:0]     ), //o
    .clk                     (clk                          ), //i
    .reset                   (reset                        )  //i
  );
  ProcessingElement_6 pes_1_0 (
    .io_inputA               (skewBuffer_2_io_output_1[7:0]), //i
    .io_inputB               (pes_0_0_io_outputB[7:0]      ), //i
    .io_inputC               (pes_0_1_io_outputC[31:0]     ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_1_0  ), //i
    .io_resetPartialC        (io_resetPartialC_1_0         ), //i
    .io_outputA              (pes_1_0_io_outputA[7:0]      ), //o
    .io_outputB              (pes_1_0_io_outputB[7:0]      ), //o
    .io_outputC              (pes_1_0_io_outputC[31:0]     ), //o
    .clk                     (clk                          ), //i
    .reset                   (reset                        )  //i
  );
  ProcessingElement_6 pes_1_1 (
    .io_inputA               (pes_1_0_io_outputA[7:0]    ), //i
    .io_inputB               (pes_0_1_io_outputB[7:0]    ), //i
    .io_inputC               (pes_0_2_io_outputC[31:0]   ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_1_1), //i
    .io_resetPartialC        (io_resetPartialC_1_1       ), //i
    .io_outputA              (pes_1_1_io_outputA[7:0]    ), //o
    .io_outputB              (pes_1_1_io_outputB[7:0]    ), //o
    .io_outputC              (pes_1_1_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_6 pes_1_2 (
    .io_inputA               (pes_1_1_io_outputA[7:0]    ), //i
    .io_inputB               (pes_0_2_io_outputB[7:0]    ), //i
    .io_inputC               (pes_0_3_io_outputC[31:0]   ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_1_2), //i
    .io_resetPartialC        (io_resetPartialC_1_2       ), //i
    .io_outputA              (pes_1_2_io_outputA[7:0]    ), //o
    .io_outputB              (pes_1_2_io_outputB[7:0]    ), //o
    .io_outputC              (pes_1_2_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_6 pes_1_3 (
    .io_inputA               (pes_1_2_io_outputA[7:0]    ), //i
    .io_inputB               (pes_0_3_io_outputB[7:0]    ), //i
    .io_inputC               (pes_0_4_io_outputC[31:0]   ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_1_3), //i
    .io_resetPartialC        (io_resetPartialC_1_3       ), //i
    .io_outputA              (pes_1_3_io_outputA[7:0]    ), //o
    .io_outputB              (pes_1_3_io_outputB[7:0]    ), //o
    .io_outputC              (pes_1_3_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_6 pes_1_4 (
    .io_inputA               (pes_1_3_io_outputA[7:0]    ), //i
    .io_inputB               (pes_0_4_io_outputB[7:0]    ), //i
    .io_inputC               (pes_0_5_io_outputC[31:0]   ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_1_4), //i
    .io_resetPartialC        (io_resetPartialC_1_4       ), //i
    .io_outputA              (pes_1_4_io_outputA[7:0]    ), //o
    .io_outputB              (pes_1_4_io_outputB[7:0]    ), //o
    .io_outputC              (pes_1_4_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_5 pes_1_5 (
    .io_inputA               (pes_1_4_io_outputA[7:0]    ), //i
    .io_inputB               (pes_0_5_io_outputB[7:0]    ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_1_5), //i
    .io_resetPartialC        (io_resetPartialC_1_5       ), //i
    .io_outputB              (pes_1_5_io_outputB[7:0]    ), //o
    .io_outputC              (pes_1_5_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_6 pes_2_0 (
    .io_inputA               (skewBuffer_2_io_output_2[7:0]), //i
    .io_inputB               (pes_1_0_io_outputB[7:0]      ), //i
    .io_inputC               (pes_1_1_io_outputC[31:0]     ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_2_0  ), //i
    .io_resetPartialC        (io_resetPartialC_2_0         ), //i
    .io_outputA              (pes_2_0_io_outputA[7:0]      ), //o
    .io_outputB              (pes_2_0_io_outputB[7:0]      ), //o
    .io_outputC              (pes_2_0_io_outputC[31:0]     ), //o
    .clk                     (clk                          ), //i
    .reset                   (reset                        )  //i
  );
  ProcessingElement_6 pes_2_1 (
    .io_inputA               (pes_2_0_io_outputA[7:0]    ), //i
    .io_inputB               (pes_1_1_io_outputB[7:0]    ), //i
    .io_inputC               (pes_1_2_io_outputC[31:0]   ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_2_1), //i
    .io_resetPartialC        (io_resetPartialC_2_1       ), //i
    .io_outputA              (pes_2_1_io_outputA[7:0]    ), //o
    .io_outputB              (pes_2_1_io_outputB[7:0]    ), //o
    .io_outputC              (pes_2_1_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_6 pes_2_2 (
    .io_inputA               (pes_2_1_io_outputA[7:0]    ), //i
    .io_inputB               (pes_1_2_io_outputB[7:0]    ), //i
    .io_inputC               (pes_1_3_io_outputC[31:0]   ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_2_2), //i
    .io_resetPartialC        (io_resetPartialC_2_2       ), //i
    .io_outputA              (pes_2_2_io_outputA[7:0]    ), //o
    .io_outputB              (pes_2_2_io_outputB[7:0]    ), //o
    .io_outputC              (pes_2_2_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_6 pes_2_3 (
    .io_inputA               (pes_2_2_io_outputA[7:0]    ), //i
    .io_inputB               (pes_1_3_io_outputB[7:0]    ), //i
    .io_inputC               (pes_1_4_io_outputC[31:0]   ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_2_3), //i
    .io_resetPartialC        (io_resetPartialC_2_3       ), //i
    .io_outputA              (pes_2_3_io_outputA[7:0]    ), //o
    .io_outputB              (pes_2_3_io_outputB[7:0]    ), //o
    .io_outputC              (pes_2_3_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_6 pes_2_4 (
    .io_inputA               (pes_2_3_io_outputA[7:0]    ), //i
    .io_inputB               (pes_1_4_io_outputB[7:0]    ), //i
    .io_inputC               (pes_1_5_io_outputC[31:0]   ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_2_4), //i
    .io_resetPartialC        (io_resetPartialC_2_4       ), //i
    .io_outputA              (pes_2_4_io_outputA[7:0]    ), //o
    .io_outputB              (pes_2_4_io_outputB[7:0]    ), //o
    .io_outputC              (pes_2_4_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_5 pes_2_5 (
    .io_inputA               (pes_2_4_io_outputA[7:0]    ), //i
    .io_inputB               (pes_1_5_io_outputB[7:0]    ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_2_5), //i
    .io_resetPartialC        (io_resetPartialC_2_5       ), //i
    .io_outputB              (pes_2_5_io_outputB[7:0]    ), //o
    .io_outputC              (pes_2_5_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_6 pes_3_0 (
    .io_inputA               (skewBuffer_2_io_output_3[7:0]), //i
    .io_inputB               (pes_2_0_io_outputB[7:0]      ), //i
    .io_inputC               (pes_2_1_io_outputC[31:0]     ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_3_0  ), //i
    .io_resetPartialC        (io_resetPartialC_3_0         ), //i
    .io_outputA              (pes_3_0_io_outputA[7:0]      ), //o
    .io_outputB              (pes_3_0_io_outputB[7:0]      ), //o
    .io_outputC              (pes_3_0_io_outputC[31:0]     ), //o
    .clk                     (clk                          ), //i
    .reset                   (reset                        )  //i
  );
  ProcessingElement_6 pes_3_1 (
    .io_inputA               (pes_3_0_io_outputA[7:0]    ), //i
    .io_inputB               (pes_2_1_io_outputB[7:0]    ), //i
    .io_inputC               (pes_2_2_io_outputC[31:0]   ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_3_1), //i
    .io_resetPartialC        (io_resetPartialC_3_1       ), //i
    .io_outputA              (pes_3_1_io_outputA[7:0]    ), //o
    .io_outputB              (pes_3_1_io_outputB[7:0]    ), //o
    .io_outputC              (pes_3_1_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_6 pes_3_2 (
    .io_inputA               (pes_3_1_io_outputA[7:0]    ), //i
    .io_inputB               (pes_2_2_io_outputB[7:0]    ), //i
    .io_inputC               (pes_2_3_io_outputC[31:0]   ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_3_2), //i
    .io_resetPartialC        (io_resetPartialC_3_2       ), //i
    .io_outputA              (pes_3_2_io_outputA[7:0]    ), //o
    .io_outputB              (pes_3_2_io_outputB[7:0]    ), //o
    .io_outputC              (pes_3_2_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_6 pes_3_3 (
    .io_inputA               (pes_3_2_io_outputA[7:0]    ), //i
    .io_inputB               (pes_2_3_io_outputB[7:0]    ), //i
    .io_inputC               (pes_2_4_io_outputC[31:0]   ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_3_3), //i
    .io_resetPartialC        (io_resetPartialC_3_3       ), //i
    .io_outputA              (pes_3_3_io_outputA[7:0]    ), //o
    .io_outputB              (pes_3_3_io_outputB[7:0]    ), //o
    .io_outputC              (pes_3_3_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_6 pes_3_4 (
    .io_inputA               (pes_3_3_io_outputA[7:0]    ), //i
    .io_inputB               (pes_2_4_io_outputB[7:0]    ), //i
    .io_inputC               (pes_2_5_io_outputC[31:0]   ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_3_4), //i
    .io_resetPartialC        (io_resetPartialC_3_4       ), //i
    .io_outputA              (pes_3_4_io_outputA[7:0]    ), //o
    .io_outputB              (pes_3_4_io_outputB[7:0]    ), //o
    .io_outputC              (pes_3_4_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_5 pes_3_5 (
    .io_inputA               (pes_3_4_io_outputA[7:0]    ), //i
    .io_inputB               (pes_2_5_io_outputB[7:0]    ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_3_5), //i
    .io_resetPartialC        (io_resetPartialC_3_5       ), //i
    .io_outputB              (pes_3_5_io_outputB[7:0]    ), //o
    .io_outputC              (pes_3_5_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_24 pes_4_0 (
    .io_inputA               (skewBuffer_2_io_output_4[7:0]), //i
    .io_inputB               (pes_3_0_io_outputB[7:0]      ), //i
    .io_inputC               (pes_3_1_io_outputC[31:0]     ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_4_0  ), //i
    .io_resetPartialC        (io_resetPartialC_4_0         ), //i
    .io_outputA              (pes_4_0_io_outputA[7:0]      ), //o
    .io_outputC              (pes_4_0_io_outputC[31:0]     ), //o
    .clk                     (clk                          ), //i
    .reset                   (reset                        )  //i
  );
  ProcessingElement_24 pes_4_1 (
    .io_inputA               (pes_4_0_io_outputA[7:0]    ), //i
    .io_inputB               (pes_3_1_io_outputB[7:0]    ), //i
    .io_inputC               (pes_3_2_io_outputC[31:0]   ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_4_1), //i
    .io_resetPartialC        (io_resetPartialC_4_1       ), //i
    .io_outputA              (pes_4_1_io_outputA[7:0]    ), //o
    .io_outputC              (pes_4_1_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_24 pes_4_2 (
    .io_inputA               (pes_4_1_io_outputA[7:0]    ), //i
    .io_inputB               (pes_3_2_io_outputB[7:0]    ), //i
    .io_inputC               (pes_3_3_io_outputC[31:0]   ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_4_2), //i
    .io_resetPartialC        (io_resetPartialC_4_2       ), //i
    .io_outputA              (pes_4_2_io_outputA[7:0]    ), //o
    .io_outputC              (pes_4_2_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_24 pes_4_3 (
    .io_inputA               (pes_4_2_io_outputA[7:0]    ), //i
    .io_inputB               (pes_3_3_io_outputB[7:0]    ), //i
    .io_inputC               (pes_3_4_io_outputC[31:0]   ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_4_3), //i
    .io_resetPartialC        (io_resetPartialC_4_3       ), //i
    .io_outputA              (pes_4_3_io_outputA[7:0]    ), //o
    .io_outputC              (pes_4_3_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_24 pes_4_4 (
    .io_inputA               (pes_4_3_io_outputA[7:0]    ), //i
    .io_inputB               (pes_3_4_io_outputB[7:0]    ), //i
    .io_inputC               (pes_3_5_io_outputC[31:0]   ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_4_4), //i
    .io_resetPartialC        (io_resetPartialC_4_4       ), //i
    .io_outputA              (pes_4_4_io_outputA[7:0]    ), //o
    .io_outputC              (pes_4_4_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  ProcessingElement_29 pes_4_5 (
    .io_inputA               (pes_4_4_io_outputA[7:0]    ), //i
    .io_inputB               (pes_3_5_io_outputB[7:0]    ), //i
    .io_outputCaptureEnableC (io_outputCaptureEnableC_4_5), //i
    .io_resetPartialC        (io_resetPartialC_4_5       ), //i
    .io_outputC              (pes_4_5_io_outputC[31:0]   ), //o
    .clk                     (clk                        ), //i
    .reset                   (reset                      )  //i
  );
  SkewBuffer skewBuffer_2 (
    .io_input_0  (io_inputA_0[7:0]             ), //i
    .io_input_1  (io_inputA_1[7:0]             ), //i
    .io_input_2  (io_inputA_2[7:0]             ), //i
    .io_input_3  (io_inputA_3[7:0]             ), //i
    .io_input_4  (io_inputA_4[7:0]             ), //i
    .io_output_0 (skewBuffer_2_io_output_0[7:0]), //o
    .io_output_1 (skewBuffer_2_io_output_1[7:0]), //o
    .io_output_2 (skewBuffer_2_io_output_2[7:0]), //o
    .io_output_3 (skewBuffer_2_io_output_3[7:0]), //o
    .io_output_4 (skewBuffer_2_io_output_4[7:0]), //o
    .clk         (clk                          ), //i
    .reset       (reset                        )  //i
  );
  SkewBuffer_1 skewBuffer_3 (
    .io_input_0  (io_inputB_0[7:0]             ), //i
    .io_input_1  (io_inputB_1[7:0]             ), //i
    .io_input_2  (io_inputB_2[7:0]             ), //i
    .io_input_3  (io_inputB_3[7:0]             ), //i
    .io_input_4  (io_inputB_4[7:0]             ), //i
    .io_input_5  (io_inputB_5[7:0]             ), //i
    .io_output_0 (skewBuffer_3_io_output_0[7:0]), //o
    .io_output_1 (skewBuffer_3_io_output_1[7:0]), //o
    .io_output_2 (skewBuffer_3_io_output_2[7:0]), //o
    .io_output_3 (skewBuffer_3_io_output_3[7:0]), //o
    .io_output_4 (skewBuffer_3_io_output_4[7:0]), //o
    .io_output_5 (skewBuffer_3_io_output_5[7:0]), //o
    .clk         (clk                          ), //i
    .reset       (reset                        )  //i
  );
  assign io_outputC_0 = pes_0_0_io_outputC;
  assign io_outputC_1 = pes_1_0_io_outputC;
  assign io_outputC_2 = pes_2_0_io_outputC;
  assign io_outputC_3 = pes_3_0_io_outputC;
  assign io_outputC_4 = pes_4_0_io_outputC;
  assign io_outputC_5 = pes_4_1_io_outputC;
  assign io_outputC_6 = pes_4_2_io_outputC;
  assign io_outputC_7 = pes_4_3_io_outputC;
  assign io_outputC_8 = pes_4_4_io_outputC;
  assign io_outputC_9 = pes_4_5_io_outputC;

endmodule

module SkewBuffer_1 (
  input  wire [7:0]    io_input_0,
  input  wire [7:0]    io_input_1,
  input  wire [7:0]    io_input_2,
  input  wire [7:0]    io_input_3,
  input  wire [7:0]    io_input_4,
  input  wire [7:0]    io_input_5,
  output wire [7:0]    io_output_0,
  output wire [7:0]    io_output_1,
  output wire [7:0]    io_output_2,
  output wire [7:0]    io_output_3,
  output wire [7:0]    io_output_4,
  output wire [7:0]    io_output_5,
  input  wire          clk,
  input  wire          reset
);

  reg        [7:0]    io_input_0_delay_1;
  reg        [7:0]    io_input_0_delay_2;
  reg        [7:0]    io_input_0_delay_3;
  reg        [7:0]    io_input_0_delay_4;
  reg        [7:0]    io_input_0_delay_5;
  reg        [7:0]    io_input_0_delay_6;
  reg        [7:0]    io_input_0_delay_7;
  reg        [7:0]    io_input_1_delay_1;
  reg        [7:0]    io_input_1_delay_2;
  reg        [7:0]    io_input_1_delay_3;
  reg        [7:0]    io_input_1_delay_4;
  reg        [7:0]    io_input_1_delay_5;
  reg        [7:0]    io_input_1_delay_6;
  reg        [7:0]    io_input_1_delay_7;
  reg        [7:0]    io_input_2_delay_1;
  reg        [7:0]    io_input_2_delay_2;
  reg        [7:0]    io_input_2_delay_3;
  reg        [7:0]    io_input_2_delay_4;
  reg        [7:0]    io_input_2_delay_5;
  reg        [7:0]    io_input_2_delay_6;
  reg        [7:0]    io_input_2_delay_7;
  reg        [7:0]    io_input_3_delay_1;
  reg        [7:0]    io_input_3_delay_2;
  reg        [7:0]    io_input_3_delay_3;
  reg        [7:0]    io_input_3_delay_4;
  reg        [7:0]    io_input_3_delay_5;
  reg        [7:0]    io_input_3_delay_6;
  reg        [7:0]    io_input_3_delay_7;
  reg        [7:0]    io_input_4_delay_1;
  reg        [7:0]    io_input_4_delay_2;
  reg        [7:0]    io_input_4_delay_3;
  reg        [7:0]    io_input_4_delay_4;
  reg        [7:0]    io_input_4_delay_5;
  reg        [7:0]    io_input_4_delay_6;
  reg        [7:0]    io_input_4_delay_7;
  reg        [7:0]    io_input_5_delay_1;
  reg        [7:0]    io_input_5_delay_2;
  reg        [7:0]    io_input_5_delay_3;
  reg        [7:0]    io_input_5_delay_4;
  reg        [7:0]    io_input_5_delay_5;
  reg        [7:0]    io_input_5_delay_6;
  reg        [7:0]    io_input_5_delay_7;

  assign io_output_0 = io_input_0_delay_7;
  assign io_output_1 = io_input_1_delay_7;
  assign io_output_2 = io_input_2_delay_7;
  assign io_output_3 = io_input_3_delay_7;
  assign io_output_4 = io_input_4_delay_7;
  assign io_output_5 = io_input_5_delay_7;
  always @(posedge clk) begin
    io_input_0_delay_1 <= io_input_0;
    io_input_0_delay_2 <= io_input_0_delay_1;
    io_input_0_delay_3 <= io_input_0_delay_2;
    io_input_0_delay_4 <= io_input_0_delay_3;
    io_input_0_delay_5 <= io_input_0_delay_4;
    io_input_0_delay_6 <= io_input_0_delay_5;
    io_input_0_delay_7 <= io_input_0_delay_6;
    io_input_1_delay_1 <= io_input_1;
    io_input_1_delay_2 <= io_input_1_delay_1;
    io_input_1_delay_3 <= io_input_1_delay_2;
    io_input_1_delay_4 <= io_input_1_delay_3;
    io_input_1_delay_5 <= io_input_1_delay_4;
    io_input_1_delay_6 <= io_input_1_delay_5;
    io_input_1_delay_7 <= io_input_1_delay_6;
    io_input_2_delay_1 <= io_input_2;
    io_input_2_delay_2 <= io_input_2_delay_1;
    io_input_2_delay_3 <= io_input_2_delay_2;
    io_input_2_delay_4 <= io_input_2_delay_3;
    io_input_2_delay_5 <= io_input_2_delay_4;
    io_input_2_delay_6 <= io_input_2_delay_5;
    io_input_2_delay_7 <= io_input_2_delay_6;
    io_input_3_delay_1 <= io_input_3;
    io_input_3_delay_2 <= io_input_3_delay_1;
    io_input_3_delay_3 <= io_input_3_delay_2;
    io_input_3_delay_4 <= io_input_3_delay_3;
    io_input_3_delay_5 <= io_input_3_delay_4;
    io_input_3_delay_6 <= io_input_3_delay_5;
    io_input_3_delay_7 <= io_input_3_delay_6;
    io_input_4_delay_1 <= io_input_4;
    io_input_4_delay_2 <= io_input_4_delay_1;
    io_input_4_delay_3 <= io_input_4_delay_2;
    io_input_4_delay_4 <= io_input_4_delay_3;
    io_input_4_delay_5 <= io_input_4_delay_4;
    io_input_4_delay_6 <= io_input_4_delay_5;
    io_input_4_delay_7 <= io_input_4_delay_6;
    io_input_5_delay_1 <= io_input_5;
    io_input_5_delay_2 <= io_input_5_delay_1;
    io_input_5_delay_3 <= io_input_5_delay_2;
    io_input_5_delay_4 <= io_input_5_delay_3;
    io_input_5_delay_5 <= io_input_5_delay_4;
    io_input_5_delay_6 <= io_input_5_delay_5;
    io_input_5_delay_7 <= io_input_5_delay_6;
  end


endmodule

module SkewBuffer (
  input  wire [7:0]    io_input_0,
  input  wire [7:0]    io_input_1,
  input  wire [7:0]    io_input_2,
  input  wire [7:0]    io_input_3,
  input  wire [7:0]    io_input_4,
  output wire [7:0]    io_output_0,
  output wire [7:0]    io_output_1,
  output wire [7:0]    io_output_2,
  output wire [7:0]    io_output_3,
  output wire [7:0]    io_output_4,
  input  wire          clk,
  input  wire          reset
);

  reg        [7:0]    io_input_0_delay_1;
  reg        [7:0]    io_input_0_delay_2;
  reg        [7:0]    io_input_0_delay_3;
  reg        [7:0]    io_input_0_delay_4;
  reg        [7:0]    io_input_0_delay_5;
  reg        [7:0]    io_input_0_delay_6;
  reg        [7:0]    io_input_1_delay_1;
  reg        [7:0]    io_input_1_delay_2;
  reg        [7:0]    io_input_1_delay_3;
  reg        [7:0]    io_input_1_delay_4;
  reg        [7:0]    io_input_1_delay_5;
  reg        [7:0]    io_input_1_delay_6;
  reg        [7:0]    io_input_2_delay_1;
  reg        [7:0]    io_input_2_delay_2;
  reg        [7:0]    io_input_2_delay_3;
  reg        [7:0]    io_input_2_delay_4;
  reg        [7:0]    io_input_2_delay_5;
  reg        [7:0]    io_input_2_delay_6;
  reg        [7:0]    io_input_3_delay_1;
  reg        [7:0]    io_input_3_delay_2;
  reg        [7:0]    io_input_3_delay_3;
  reg        [7:0]    io_input_3_delay_4;
  reg        [7:0]    io_input_3_delay_5;
  reg        [7:0]    io_input_3_delay_6;
  reg        [7:0]    io_input_4_delay_1;
  reg        [7:0]    io_input_4_delay_2;
  reg        [7:0]    io_input_4_delay_3;
  reg        [7:0]    io_input_4_delay_4;
  reg        [7:0]    io_input_4_delay_5;
  reg        [7:0]    io_input_4_delay_6;

  assign io_output_0 = io_input_0_delay_6;
  assign io_output_1 = io_input_1_delay_6;
  assign io_output_2 = io_input_2_delay_6;
  assign io_output_3 = io_input_3_delay_6;
  assign io_output_4 = io_input_4_delay_6;
  always @(posedge clk) begin
    io_input_0_delay_1 <= io_input_0;
    io_input_0_delay_2 <= io_input_0_delay_1;
    io_input_0_delay_3 <= io_input_0_delay_2;
    io_input_0_delay_4 <= io_input_0_delay_3;
    io_input_0_delay_5 <= io_input_0_delay_4;
    io_input_0_delay_6 <= io_input_0_delay_5;
    io_input_1_delay_1 <= io_input_1;
    io_input_1_delay_2 <= io_input_1_delay_1;
    io_input_1_delay_3 <= io_input_1_delay_2;
    io_input_1_delay_4 <= io_input_1_delay_3;
    io_input_1_delay_5 <= io_input_1_delay_4;
    io_input_1_delay_6 <= io_input_1_delay_5;
    io_input_2_delay_1 <= io_input_2;
    io_input_2_delay_2 <= io_input_2_delay_1;
    io_input_2_delay_3 <= io_input_2_delay_2;
    io_input_2_delay_4 <= io_input_2_delay_3;
    io_input_2_delay_5 <= io_input_2_delay_4;
    io_input_2_delay_6 <= io_input_2_delay_5;
    io_input_3_delay_1 <= io_input_3;
    io_input_3_delay_2 <= io_input_3_delay_1;
    io_input_3_delay_3 <= io_input_3_delay_2;
    io_input_3_delay_4 <= io_input_3_delay_3;
    io_input_3_delay_5 <= io_input_3_delay_4;
    io_input_3_delay_6 <= io_input_3_delay_5;
    io_input_4_delay_1 <= io_input_4;
    io_input_4_delay_2 <= io_input_4_delay_1;
    io_input_4_delay_3 <= io_input_4_delay_2;
    io_input_4_delay_4 <= io_input_4_delay_3;
    io_input_4_delay_5 <= io_input_4_delay_4;
    io_input_4_delay_6 <= io_input_4_delay_5;
  end


endmodule

module ProcessingElement_29 (
  input  wire [7:0]    io_inputA,
  input  wire [7:0]    io_inputB,
  input  wire          io_outputCaptureEnableC,
  input  wire          io_resetPartialC,
  output wire [31:0]   io_outputC,
  input  wire          clk,
  input  wire          reset
);

  wire       [31:0]   _zz__zz_io_outputC_1;
  wire       [31:0]   _zz__zz_io_outputC_1_1;
  wire       [31:0]   _zz__zz_io_outputC_1_2;
  wire       [15:0]   _zz_io_outputC;
  reg        [31:0]   _zz_io_outputC_1;

  assign _zz__zz_io_outputC_1 = {{16{_zz_io_outputC[15]}}, _zz_io_outputC};
  assign _zz__zz_io_outputC_1_1 = ($signed(_zz__zz_io_outputC_1_2) + $signed(_zz_io_outputC_1));
  assign _zz__zz_io_outputC_1_2 = {{16{_zz_io_outputC[15]}}, _zz_io_outputC};
  assign _zz_io_outputC = ($signed(io_inputA) * $signed(io_inputB));
  assign io_outputC = _zz_io_outputC_1;
  always @(posedge clk or posedge reset) begin
    if(reset) begin
      _zz_io_outputC_1 <= 32'h0;
    end else begin
      _zz_io_outputC_1 <= (io_resetPartialC ? _zz__zz_io_outputC_1 : _zz__zz_io_outputC_1_1);
    end
  end


endmodule

//ProcessingElement_28 replaced by ProcessingElement_24

//ProcessingElement_27 replaced by ProcessingElement_24

//ProcessingElement_26 replaced by ProcessingElement_24

//ProcessingElement_25 replaced by ProcessingElement_24

module ProcessingElement_24 (
  input  wire [7:0]    io_inputA,
  input  wire [7:0]    io_inputB,
  input  wire [31:0]   io_inputC,
  input  wire          io_outputCaptureEnableC,
  input  wire          io_resetPartialC,
  output wire [7:0]    io_outputA,
  output wire [31:0]   io_outputC,
  input  wire          clk,
  input  wire          reset
);

  wire       [31:0]   _zz__zz_io_outputC_1;
  wire       [31:0]   _zz__zz_io_outputC_1_1;
  wire       [31:0]   _zz__zz_io_outputC_1_2;
  reg        [7:0]    io_inputA_regNext;
  wire       [15:0]   _zz_io_outputC;
  reg        [31:0]   _zz_io_outputC_1;

  assign _zz__zz_io_outputC_1 = {{16{_zz_io_outputC[15]}}, _zz_io_outputC};
  assign _zz__zz_io_outputC_1_1 = ($signed(_zz__zz_io_outputC_1_2) + $signed(_zz_io_outputC_1));
  assign _zz__zz_io_outputC_1_2 = {{16{_zz_io_outputC[15]}}, _zz_io_outputC};
  assign io_outputA = io_inputA_regNext;
  assign _zz_io_outputC = ($signed(io_inputA) * $signed(io_inputB));
  assign io_outputC = (io_outputCaptureEnableC ? _zz_io_outputC_1 : io_inputC);
  always @(posedge clk or posedge reset) begin
    if(reset) begin
      io_inputA_regNext <= 8'h0;
      _zz_io_outputC_1 <= 32'h0;
    end else begin
      io_inputA_regNext <= io_inputA;
      _zz_io_outputC_1 <= (io_resetPartialC ? _zz__zz_io_outputC_1 : _zz__zz_io_outputC_1_1);
    end
  end


endmodule

//ProcessingElement_23 replaced by ProcessingElement_5

//ProcessingElement_22 replaced by ProcessingElement_6

//ProcessingElement_21 replaced by ProcessingElement_6

//ProcessingElement_20 replaced by ProcessingElement_6

//ProcessingElement_19 replaced by ProcessingElement_6

//ProcessingElement_18 replaced by ProcessingElement_6

//ProcessingElement_17 replaced by ProcessingElement_5

//ProcessingElement_16 replaced by ProcessingElement_6

//ProcessingElement_15 replaced by ProcessingElement_6

//ProcessingElement_14 replaced by ProcessingElement_6

//ProcessingElement_13 replaced by ProcessingElement_6

//ProcessingElement_12 replaced by ProcessingElement_6

//ProcessingElement_11 replaced by ProcessingElement_5

//ProcessingElement_10 replaced by ProcessingElement_6

//ProcessingElement_9 replaced by ProcessingElement_6

//ProcessingElement_8 replaced by ProcessingElement_6

//ProcessingElement_7 replaced by ProcessingElement_6

module ProcessingElement_6 (
  input  wire [7:0]    io_inputA,
  input  wire [7:0]    io_inputB,
  input  wire [31:0]   io_inputC,
  input  wire          io_outputCaptureEnableC,
  input  wire          io_resetPartialC,
  output wire [7:0]    io_outputA,
  output wire [7:0]    io_outputB,
  output wire [31:0]   io_outputC,
  input  wire          clk,
  input  wire          reset
);

  wire       [31:0]   _zz__zz_io_outputC_1;
  wire       [31:0]   _zz__zz_io_outputC_1_1;
  wire       [31:0]   _zz__zz_io_outputC_1_2;
  reg        [7:0]    io_inputA_regNext;
  reg        [7:0]    io_inputB_regNext;
  wire       [15:0]   _zz_io_outputC;
  reg        [31:0]   _zz_io_outputC_1;

  assign _zz__zz_io_outputC_1 = {{16{_zz_io_outputC[15]}}, _zz_io_outputC};
  assign _zz__zz_io_outputC_1_1 = ($signed(_zz__zz_io_outputC_1_2) + $signed(_zz_io_outputC_1));
  assign _zz__zz_io_outputC_1_2 = {{16{_zz_io_outputC[15]}}, _zz_io_outputC};
  assign io_outputA = io_inputA_regNext;
  assign io_outputB = io_inputB_regNext;
  assign _zz_io_outputC = ($signed(io_inputA) * $signed(io_inputB));
  assign io_outputC = (io_outputCaptureEnableC ? _zz_io_outputC_1 : io_inputC);
  always @(posedge clk or posedge reset) begin
    if(reset) begin
      io_inputA_regNext <= 8'h0;
      io_inputB_regNext <= 8'h0;
      _zz_io_outputC_1 <= 32'h0;
    end else begin
      io_inputA_regNext <= io_inputA;
      io_inputB_regNext <= io_inputB;
      _zz_io_outputC_1 <= (io_resetPartialC ? _zz__zz_io_outputC_1 : _zz__zz_io_outputC_1_1);
    end
  end


endmodule

module ProcessingElement_5 (
  input  wire [7:0]    io_inputA,
  input  wire [7:0]    io_inputB,
  input  wire          io_outputCaptureEnableC,
  input  wire          io_resetPartialC,
  output wire [7:0]    io_outputB,
  output wire [31:0]   io_outputC,
  input  wire          clk,
  input  wire          reset
);

  wire       [31:0]   _zz__zz_io_outputC_1;
  wire       [31:0]   _zz__zz_io_outputC_1_1;
  wire       [31:0]   _zz__zz_io_outputC_1_2;
  reg        [7:0]    io_inputB_regNext;
  wire       [15:0]   _zz_io_outputC;
  reg        [31:0]   _zz_io_outputC_1;

  assign _zz__zz_io_outputC_1 = {{16{_zz_io_outputC[15]}}, _zz_io_outputC};
  assign _zz__zz_io_outputC_1_1 = ($signed(_zz__zz_io_outputC_1_2) + $signed(_zz_io_outputC_1));
  assign _zz__zz_io_outputC_1_2 = {{16{_zz_io_outputC[15]}}, _zz_io_outputC};
  assign io_outputB = io_inputB_regNext;
  assign _zz_io_outputC = ($signed(io_inputA) * $signed(io_inputB));
  assign io_outputC = _zz_io_outputC_1;
  always @(posedge clk or posedge reset) begin
    if(reset) begin
      io_inputB_regNext <= 8'h0;
      _zz_io_outputC_1 <= 32'h0;
    end else begin
      io_inputB_regNext <= io_inputB;
      _zz_io_outputC_1 <= (io_resetPartialC ? _zz__zz_io_outputC_1 : _zz__zz_io_outputC_1_1);
    end
  end


endmodule

//ProcessingElement_4 replaced by ProcessingElement

//ProcessingElement_3 replaced by ProcessingElement

//ProcessingElement_2 replaced by ProcessingElement

//ProcessingElement_1 replaced by ProcessingElement

module ProcessingElement (
  input  wire [7:0]    io_inputA,
  input  wire [7:0]    io_inputB,
  input  wire          io_outputCaptureEnableC,
  input  wire          io_resetPartialC,
  output wire [7:0]    io_outputA,
  output wire [7:0]    io_outputB,
  output wire [31:0]   io_outputC,
  input  wire          clk,
  input  wire          reset
);

  wire       [31:0]   _zz__zz_io_outputC_1;
  wire       [31:0]   _zz__zz_io_outputC_1_1;
  wire       [31:0]   _zz__zz_io_outputC_1_2;
  reg        [7:0]    io_inputA_regNext;
  reg        [7:0]    io_inputB_regNext;
  wire       [15:0]   _zz_io_outputC;
  reg        [31:0]   _zz_io_outputC_1;

  assign _zz__zz_io_outputC_1 = {{16{_zz_io_outputC[15]}}, _zz_io_outputC};
  assign _zz__zz_io_outputC_1_1 = ($signed(_zz__zz_io_outputC_1_2) + $signed(_zz_io_outputC_1));
  assign _zz__zz_io_outputC_1_2 = {{16{_zz_io_outputC[15]}}, _zz_io_outputC};
  assign io_outputA = io_inputA_regNext;
  assign io_outputB = io_inputB_regNext;
  assign _zz_io_outputC = ($signed(io_inputA) * $signed(io_inputB));
  assign io_outputC = _zz_io_outputC_1;
  always @(posedge clk or posedge reset) begin
    if(reset) begin
      io_inputA_regNext <= 8'h0;
      io_inputB_regNext <= 8'h0;
      _zz_io_outputC_1 <= 32'h0;
    end else begin
      io_inputA_regNext <= io_inputA;
      io_inputB_regNext <= io_inputB;
      _zz_io_outputC_1 <= (io_resetPartialC ? _zz__zz_io_outputC_1 : _zz__zz_io_outputC_1_1);
    end
  end


endmodule
