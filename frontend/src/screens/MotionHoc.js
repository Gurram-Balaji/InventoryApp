
import { motion } from "framer-motion";

const MotionHoc = (Component) => {
  return function HOC() {
    return (
      <motion.div
        initial={{ x: "-100vw", opacity: 0 }}
        animate={{ x: 0, scale: 1, opacity: 1, transition: { duration: 1.2, type: "spring" } }}
      >
        <Component />
      </motion.div>
    );
  };
};

export default MotionHoc;
