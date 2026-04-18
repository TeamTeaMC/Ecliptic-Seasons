package com.teamtea.eclipticseasons.data.api;

import net.minecraft.data.PackOutput;
import org.jspecify.annotations.NonNull;

import java.nio.file.Path;

public class MutablePackOutput extends PackOutput {
    private PackOutput original;
    private Path outputFolder;

    public MutablePackOutput(PackOutput original) {
        super(original.getOutputFolder());
        this.original = original;
        this.outputFolder = original.getOutputFolder();
    }

    public MutablePackOutput(PackOutput original, Path path) {
        super(original.getOutputFolder());
        this.original = original;
        this.outputFolder = path;
    }

    public MutablePackOutput set(Path newFolder) {
        this.outputFolder = newFolder;
        this.original = new PackOutput(outputFolder);
        return this;
    }

    public MutablePackOutput move(Path newFolder) {
        return new MutablePackOutput(this.original, this.original.getOutputFolder().resolve(newFolder));
    }

    public void reset() {
        this.outputFolder = this.original.getOutputFolder();
    }

    @Override
    public @NonNull Path getOutputFolder() {
        return outputFolder;
    }

}
