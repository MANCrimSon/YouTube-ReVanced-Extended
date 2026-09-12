package app.morphe.tools;

import com.android.tools.smali.dexlib2.DexFileFactory;
import com.android.tools.smali.dexlib2.Opcodes;
import com.android.tools.smali.dexlib2.iface.DexFile;
import com.android.tools.smali.dexlib2.iface.instruction.Instruction;
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction21c;
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction31c;
import com.android.tools.smali.dexlib2.iface.reference.StringReference;
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21c;
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction31c;
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableStringReference;
import com.android.tools.smali.dexlib2.rewriter.DexRewriter;
import com.android.tools.smali.dexlib2.rewriter.InstructionRewriter;
import com.android.tools.smali.dexlib2.rewriter.Rewriter;
import com.android.tools.smali.dexlib2.rewriter.RewriterModule;
import com.android.tools.smali.dexlib2.rewriter.Rewriters;
import com.android.tools.smali.dexlib2.writer.pool.DexPool;

import java.io.File;

/**
 * Rewrites Morphe patch update route URLs inside an extension DEX file to point
 * to a fork repository instead of the upstream MorpheApp repository.
 */
public class RouteRedirector {

    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Usage: RouteRedirector <input_dex> <output_dex> <from_repo> <to_repo>");
            System.exit(1);
        }

        File inputFile = new File(args[0]);
        File outputFile = new File(args[1]);
        final String fromPrefix = "/" + args[2] + "/refs/heads/";
        final String toPrefix = "/" + args[3] + "/refs/heads/";

        if (!inputFile.exists()) {
            System.err.println("Input file not found: " + inputFile.getPath());
            System.exit(1);
        }

        try {
            DexFile dexFile = DexFileFactory.loadDexFile(inputFile, Opcodes.getDefault());
            final int[] modifiedCount = new int[1];

            DexRewriter rewriter = new DexRewriter(new RewriterModule() {
                @Override
                public Rewriter<Instruction> getInstructionRewriter(Rewriters rewriters) {
                    return new InstructionRewriter(rewriters) {
                        @Override
                        public Instruction rewrite(Instruction instruction) {
                            if (instruction instanceof Instruction21c) {
                                Instruction21c inst = (Instruction21c) instruction;
                                if (inst.getReference() instanceof StringReference) {
                                    String str = ((StringReference) inst.getReference()).getString();
                                    if (str.contains(fromPrefix)) {
                                        String rewritten = str.replace(fromPrefix, toPrefix);
                                        modifiedCount[0]++;
                                        System.out.println("[RouteRedirector] Rewrote: " + str + " -> " + rewritten);
                                        return new ImmutableInstruction21c(inst.getOpcode(), inst.getRegisterA(), new ImmutableStringReference(rewritten));
                                    }
                                }
                            } else if (instruction instanceof Instruction31c) {
                                Instruction31c inst = (Instruction31c) instruction;
                                if (inst.getReference() instanceof StringReference) {
                                    String str = ((StringReference) inst.getReference()).getString();
                                    if (str.contains(fromPrefix)) {
                                        String rewritten = str.replace(fromPrefix, toPrefix);
                                        modifiedCount[0]++;
                                        System.out.println("[RouteRedirector] Rewrote jumbo: " + str + " -> " + rewritten);
                                        return new ImmutableInstruction31c(inst.getOpcode(), inst.getRegisterA(), new ImmutableStringReference(rewritten));
                                    }
                                }
                            }
                            return super.rewrite(instruction);
                        }
                    };
                }
            });

            DexFile rewrittenDex = rewriter.getDexFileRewriter().rewrite(dexFile);
            File tempOutputFile = new File(outputFile.getAbsolutePath() + ".tmp");
            DexPool.writeTo(tempOutputFile.getAbsolutePath(), rewrittenDex);

            if (outputFile.exists() && !outputFile.delete()) {
                System.err.println("Failed to delete existing output file: " + outputFile.getPath());
                System.exit(1);
            }
            if (!tempOutputFile.renameTo(outputFile)) {
                System.err.println("Failed to rename temporary DEX file to output destination");
                System.exit(1);
            }

            System.out.println("[RouteRedirector] Successfully processed " + modifiedCount[0] + " routes in " + outputFile.getName());
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
