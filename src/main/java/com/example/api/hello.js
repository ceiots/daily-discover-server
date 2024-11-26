// api/hello.js
module.exports = async (req, res) => {
    const response = await fetch('springboot-c6pzt54i9-ceiots-126coms-projects.vercel.app');
    const data = await response.text();
    res.status(200).json({ message: data });
};