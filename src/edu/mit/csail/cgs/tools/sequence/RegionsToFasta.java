package edu.mit.csail.cgs.tools.sequence;

import java.io.*;
import edu.mit.csail.cgs.ewok.verbs.RegionParser;
import edu.mit.csail.cgs.ewok.verbs.FastaWriter;
import edu.mit.csail.cgs.datasets.general.Region;
import edu.mit.csail.cgs.datasets.general.StrandedRegion;
import edu.mit.csail.cgs.datasets.general.NamedStrandedRegion;
import edu.mit.csail.cgs.datasets.species.Organism;
import edu.mit.csail.cgs.datasets.species.Genome;
import edu.mit.csail.cgs.tools.utils.Args;
import edu.mit.csail.cgs.utils.Pair;

/* reads a list of regions (one per line) on STDIN.
   Produces on stdout a fasta file containing those regions.
   The genome is specified on the command line as
   --species "Mm;mm8"
*/

public class RegionsToFasta {

    public static void main(String args[]) {
        try {
            Pair<Organism,Genome> pair = Args.parseGenome(args);
            Organism organism = pair.car();
            Genome genome = pair.cdr();
            FastaWriter writer = new FastaWriter(System.out);
            writer.useCache(Args.parseFlags(args).contains("cache"));
            int expand = Args.parseInteger(args,"expand",0);
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                StrandedRegion sr = StrandedRegion.fromString(genome, line);
                if (sr != null) {
                    String pieces[] = line.split("\\t");
                    if (pieces.length > 1) {
                        sr = new NamedStrandedRegion(sr, pieces[1], sr.getStrand());
                    }
                    writer.consume(sr.expand(expand,expand));
                } else {
                    Region r = Region.fromString(genome,line);
                    writer.consume(r.expand(expand,expand));   
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
